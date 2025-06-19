/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers


import play.api.data.FormError
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanInitiateResponse, UploadViewModel, UpscanRecord}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{UploadBusinessRatesBillView, UploadedBusinessRatesBillView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.UpscanRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadBusinessRatesBillController @Inject()(uploadView: UploadBusinessRatesBillView,
                                                  upscanConnector: UpscanConnector,
                                                  upscanRepo: UpscanRepo,
                                                  uploadForm: UploadForm,
                                                  authenticate: AuthRetrievals,
                                                  isRegisteredCheck: RegistrationAction,
                                                  mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {
  //http://localhost:1504/ngr-property-linking-frontend/upload-business-rates-bill?errorMessage=%27file%27+field+not+found&key=f8ad3406-3992-4d0e-ba36-8ffa339707af&errorCode=InvalidArgument&errorRequestId=SomeRequestId&errorResource=NoFileReference
  //http: //localhost:1504/ngr-property-linking-frontend/upload-business-rates-bill?errorMessage=Your+proposed+upload+exceeds+the+maximum+allowed+size&key=b3368bf1-20d6-4421-a653-964c3529fb00&errorCode=EntityTooLarge&errorRequestId=SomeRequestId&errorResource=NoFileReference
  //http://localhost:1504/ngr-property-linking-frontend/upload-business-rates-bill?errorMessage=Your+proposed+upload+is+smaller+than+the+minimum+allowed+size&key=619ce5c8-a28c-4641-9957-01c6c50818ac&errorCode=EntityTooSmall&errorRequestId=SomeRequestId&errorResource=NoFileReference
  def show(errorCode: Option[String]): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      //Error scenarios 6 (too big) EntityTooLarge, 7 (no file) InvalidArgument, 11 (too small) EntityTooSmall
      println(Console.MAGENTA + "QQQQQQQQQ" + errorCode.getOrElse("FAILED to get errorCodeString"))

      val errorToDisplay: Option[String] = errorCode match {
        case Some("InvalidArgument") => Some(Messages("uploadBusinessRatesBill.error.noFileSelected"))
        case Some("EntityTooLarge") => Some(Messages("uploadBusinessRatesBill.error.exceedsMaximumSize"))
        case Some("EntityTooSmall") => Some(Messages("uploadBusinessRatesBill.error.fileTooSmall"))
        case Some("QUARANTINE") => Some(Messages("uploadBusinessRatesBill.error.virusDetected"))
        case Some("REJECTED") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) if reason.startsWith("UNKNOWN") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) => throw new RuntimeException(s"Error in errorToDisplay: unrecognisable error from upscan '$reason'")
        case None => None
      }

      request.credId match {
        case Some(rawCredId) =>
          val credId = CredId(rawCredId)
          upscanConnector.initiate.flatMap { upscanInitiateResponse =>
            println("XXXX upscan reference on initiate is: " + upscanInitiateResponse.reference.value)

            val upscanRecord = UpscanRecord(
              credId = credId,
              reference = upscanInitiateResponse.reference,
              status = "INITIATED", // TODO: replace with status classes
              downloadUrl = None,
              fileName = None,
              failureReason = None,
              failureMessage = None
            )

            upscanRepo.upsertUpscanRecord(upscanRecord).map { _ =>
              //TODO refactor out preparedUpload
              Ok(uploadView(uploadForm(), upscanInitiateResponse, errorToDisplay, createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl))
            }
          }

        case None =>
          Future.failed(new RuntimeException("Missing credId in authenticated request"))
      }
    }
}
