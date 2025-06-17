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
import play.api.i18n.I18nSupport
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{PreparedUpload, UploadViewModel}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanCallback, UpscanCallbackSuccess, UpscanCallbackFailure, UpscanRecord, Reference}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{UploadBusinessRatesBillView, UploadedBusinessRatesBillView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.UpscanRepo

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpscanCallbackController @Inject()(upscanRepo: UpscanRepo, mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  case class UpscanRecord(reference: Reference,
                          status: String,
                          downloadUrl: Option[String],
                          fileName: Option[String],
                          failureReason: Option[String],
                          failureMessage: Option[String])

  def handleUpscanCallback: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[UpscanCallback] { callback: UpscanCallback =>
      val upscanRecord: UpscanRecord = callback match {
        case successCallback: UpscanCallbackSuccess =>
          UpscanRecord(
            reference = successCallback.reference,
            status = "READY",
            downloadUrl = successCallback.downloadUrl,
            fileName = successCallback.uploadDetails.fileName,
            failureReason = None,
            failureMessage = None)
        case failureCallback: UpscanCallbackFailure =>
          UpscanRecord(
            reference = successCallback.reference,
            status = "FAILED",
            downloadUrl = None,
            fileName = None,
            failureReason = failureCallback.failureDetails.failureReason,
            failureMessage = failureCallback.failureDetails.message)
      }
      upscanRepo.upsertUpscanRecord(upscanRecord)
      //uploadService.registerUploadResult(callback.reference, upscanRecord).map(_ => Ok)
    }
  }
}

