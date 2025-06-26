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

package uk.gov.hmrc.ngrpropertylinkingfrontend.internalcontrollers

import play.api.libs.json.JsValue
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanCallback, UpscanCallbackFailure, UpscanCallbackSuccess, UpscanRecord}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.UpscanRepo
import play.api.mvc.{Action, MessagesControllerComponents, Result}
import play.api.i18n.I18nSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpscanCallbackController @Inject()(
                                          upscanRepo: UpscanRepo,
                                          authenticate: AuthRetrievals,
                                          isRegisteredCheck: RegistrationAction,
                                          mcc: MessagesControllerComponents
                                        )(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val allowedMimeTypes: Set[String] = Set(
    "application/pdf",
    "image/png"
  )

  def handleUpscanCallback: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[UpscanCallback] {
      case success @ UpscanCallbackSuccess(reference, _, uploadDetails) =>
        val validatedCallback =
          if (allowedMimeTypes.contains(uploadDetails.fileMimeType)) {
            success
          } else {
            UpscanCallbackFailure(
              reference,
              UpscanCallBackErrorDetails("InvalidFileType", "User has uploaded unsupported file type")
            )
          }
        processCallback(validatedCallback)

      case failure: UpscanCallbackFailure =>
        processCallback(failure)
    }
  }

  private def processCallback(callback: UpscanCallback): Future[Result] = {
    upscanRepo.findByReference(callback.reference).flatMap {
      case Some(existingUpscanRecord) =>
        val updatedRecord = buildUpdatedUpscanRecord(callback, existingUpscanRecord.credId)
        upscanRepo.upsertUpscanRecord(updatedRecord).map(_ => Ok)

      case None =>
        Future.failed(new RuntimeException(s"Upscan record not found for reference: ${callback.reference.value}"))
    }
  }

  private def buildUpdatedUpscanRecord(callback: UpscanCallback, credId: CredId): UpscanRecord = callback match {
    case success: UpscanCallbackSuccess =>
      UpscanRecord(
        credId = credId,
        reference = success.reference,
        status = "READY",
        downloadUrl = Some(success.downloadUrl.toString),
        fileName = Some(success.uploadDetails.fileName),
        failureReason = None,
        failureMessage = None
      )

    case failure: UpscanCallbackFailure =>
      UpscanRecord(
        credId = credId,
        reference = failure.reference,
        status = "FAILED",
        downloadUrl = None,
        fileName = None,
        failureReason = Some(failure.failureDetails.failureReason),
        failureMessage = Some(failure.failureDetails.message)
      )
  }
}

