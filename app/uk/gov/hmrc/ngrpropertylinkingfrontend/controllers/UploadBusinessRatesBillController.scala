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

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanRecord
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{PropertyLinkingRepo, UpscanRepo}
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
                                                  propertyLinkingRepo: PropertyLinkingRepo,
                                                  mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {
  
  val attributes: Map[String, String] = Map(
    "accept" -> ".pdf,.png,.docx",
    "data-max-file-size" -> "1000000000000000",
    "data-min-file-size" -> "1000000000000000"
  )
  
  def show(errorCode: Option[String]): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      println(Console.MAGENTA + errorCode + Console.RESET)
      val errorToDisplay: Option[String] = errorCode match {
        case Some("InvalidArgument") => Some(Messages("uploadBusinessRatesBill.error.noFileSelected"))
        case Some("EntityTooLarge") => Some(Messages("uploadBusinessRatesBill.error.exceedsMaximumSize"))
        case Some("EntityTooSmall") => Some(Messages("uploadBusinessRatesBill.error.fileTooSmall"))
        case Some("InvalidFileType") => Some(Messages("uploadBusinessRatesBill.error.invalidFileType"))
        case Some("QUARANTINE") => Some(Messages("uploadBusinessRatesBill.error.virusDetected"))
        case Some("REJECTED") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) if reason.startsWith("UNKNOWN") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) => throw new RuntimeException(s"Error in errorToDisplay: unrecognisable error from upscan '$reason'")
        case None => None
      }

      request.credId match {
        case Some(rawCredId) =>
          val credId = CredId(rawCredId)

          val upload: Future[Result] = for {
            upscanInitiateResponse <- upscanConnector.initiate
            upscanRecord = UpscanRecord(
              credId = credId,
              reference = upscanInitiateResponse.reference,
              status = "INITIATED", // TODO: replace with status classes?
              downloadUrl = None,
              fileName = None,
              failureReason = None,
              failureMessage = None
            )
            _ <- upscanRepo.upsertUpscanRecord(upscanRecord)
            propertyOpt <- propertyLinkingRepo.findByCredId(credId)
          } yield {
            propertyOpt match {
              case Some(property) =>
                Ok(
                  uploadView(
                    uploadForm(),
                    upscanInitiateResponse,
                    attributes,
                    errorToDisplay,
                    upload = , 
                    createDefaultNavBar,
                    routes.FindAPropertyController.show.url,
                    appConfig.ngrDashboardUrl
                  )
                )
              case None =>
                NotFound("Property not found") // or appropriate error handling
            }
          }
        case None =>
          Future.failed(throw new NotFoundException("Missing credId in authenticated request"))
      }
    }
}
