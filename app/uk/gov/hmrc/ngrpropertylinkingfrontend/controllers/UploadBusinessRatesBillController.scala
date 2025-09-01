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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{Reference, UploadId}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.UploadProgressTracker
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadBusinessRatesBillController @Inject()(uploadView: UploadBusinessRatesBillView,
                                                  upScanConnector: UpscanConnector,
                                                  uploadProgressTracker: UploadProgressTracker,
                                                  uploadForm: UploadForm,
                                                  authenticate: AuthRetrievals,
                                                  isRegisteredCheck: RegistrationAction,
                                                  propertyLinkingRepo: PropertyLinkingRepo,
                                                  mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  val attributes: Map[String, String] = Map(
    "accept" -> ".pdf,.png,.jpg,.jpeg",
    "data-max-file-size" -> "100000000",
    "data-min-file-size" -> "1000"
  )

  def show(errorCode: Option[String], evidence: Option[String]): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
          val errorToDisplay: Option[String] = renderError(errorCode)
          val uploadId = UploadId.generate()
          val successRedirectUrl = s"${appConfig.uploadRedirectTargetBase}${routes.UploadedBusinessRatesBillController.show(uploadId, evidence).url}"
          val evidenceParameter = evidence.map(evidenceValue => s"?evidence=$evidenceValue").getOrElse("")
          val errorRedirectUrl = s"${appConfig.ngrPropertyLinkingFrontendUrl}/upload-business-rates-bill$evidenceParameter"

          for
            upscanInitiateResponse <- upScanConnector.initiate(Some(successRedirectUrl), Some(errorRedirectUrl))
            maybeProperty <- propertyLinkingRepo.findByCredId(CredId(request.credId))
            _ <- uploadProgressTracker.requestUpload(uploadId, Reference(upscanInitiateResponse.fileReference.reference))
          yield Ok(uploadView(uploadForm(),
            upscanInitiateResponse,
            attributes,
            errorToDisplay,
            maybeProperty.map(_.vmvProperty.addressFull).getOrElse(throw new NotFoundException("Not found property on account")),
            createDefaultNavBar,
            routes.FindAPropertyController.show.url,
            appConfig.ngrDashboardUrl,
            evidence)
          )
    }
  }

  def showOld(errorCode: Option[String], evidence: Option[String]): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
          val errorToDisplay: Option[String] = renderError(errorCode)
          val uploadId = UploadId.generate()
          val successRedirectUrl = s"${appConfig.uploadRedirectTargetBase}${routes.UploadedBusinessRatesBillController.show(uploadId, evidence).url}"
          val evidenceParameter = evidence.map(evidenceValue => s"?evidence=$evidenceValue").getOrElse("")
          val errorRedirectUrl = s"${appConfig.ngrPropertyLinkingFrontendUrl}/upload-business-rates-bill$evidenceParameter"

          for
            upscanInitiateResponse <- upScanConnector.initiate(Some(successRedirectUrl), Some(errorRedirectUrl))
            maybeProperty <- propertyLinkingRepo.findByCredId(CredId(request.credId))
            _ <- uploadProgressTracker.requestUpload(uploadId, Reference(upscanInitiateResponse.fileReference.reference))
          yield Ok(uploadView(uploadForm(),
            upscanInitiateResponse,
            attributes,
            errorToDisplay,
            maybeProperty.map(_.vmvProperty.addressFull).getOrElse(throw new NotFoundException("Not found property on account")),
            createDefaultNavBar,
            routes.FindAPropertyController.show.url,
            appConfig.ngrDashboardUrl,
            evidence)
          )
    }
  }
    private def renderError(errorCode: Option[String])(implicit messages: Messages) : Option[String] = {
      errorCode match {
        case Some("InvalidArgument") => Some(Messages("uploadBusinessRatesBill.error.noFileSelected"))
        case Some("EntityTooLarge") => Some(Messages("uploadBusinessRatesBill.error.exceedsMaximumSize"))
        case Some("EntityTooSmall") => Some(Messages("uploadBusinessRatesBill.error.noFileSelected"))
        case Some("InvalidFileType") => Some(Messages("uploadBusinessRatesBill.error.invalidFileType"))
        case Some("QUARANTINE") => Some(Messages("uploadBusinessRatesBill.error.virusDetected"))
        case Some("REJECTED") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) if reason.startsWith("UNKNOWN") => Some(Messages("uploadBusinessRatesBill.error.problemWithUpload"))
        case Some(reason) => throw new RuntimeException(s"Error in errorToDisplay: unrecognisable error from upscan '$reason'")
        case None => None
      }
  }
}