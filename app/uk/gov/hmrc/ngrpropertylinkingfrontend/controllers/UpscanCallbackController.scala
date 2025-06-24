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

import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc.{Action, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanCallback, UpscanCallbackFailure, UpscanCallbackSuccess, UpscanRecord}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.UpscanRepo
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.api.data.FormError
import play.api.i18n.I18nSupport
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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpscanCallbackController @Inject()(upscanRepo: UpscanRepo,
                                         authenticate: AuthRetrievals,
                                         isRegisteredCheck: RegistrationAction,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {
//TODO move some to service?
  def handleUpscanCallback: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[UpscanCallback] { upscanCallback =>
      upscanRepo.findByReference(upscanCallback.reference).flatMap {
        case Some(existingUpscanRecord) =>
          val updatedRecord: UpscanRecord = buildUpdatedUpscanRecord(upscanCallback, existingUpscanRecord.credId)
          upscanRepo.upsertUpscanRecord(updatedRecord).map(_ => Ok)
        case None =>
          Future.failed(new RuntimeException("Upscan record not found for reference: " + upscanCallback.reference.value))
      }
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

