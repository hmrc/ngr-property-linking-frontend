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

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
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
              Ok(uploadView(uploadForm(), upscanInitiateResponse, createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl))
            }
          }

        case None =>
          Future.failed(new RuntimeException("Missing credId in authenticated request"))
      }
    }
}
