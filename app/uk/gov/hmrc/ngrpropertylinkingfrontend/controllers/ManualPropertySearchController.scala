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
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction, RegistrationAndPropertyLinkCheckAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.FindAPropertyConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit.ManualPropertySearchAuditModel
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ManualPropertySearchForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.LookUpVMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FindAPropertyRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.AuditingService
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ManualPropertySearchView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ManualPropertySearchController @Inject()(manualPropertySearchView: ManualPropertySearchView,
                                               authenticate: AuthRetrievals,
                                               mandatoryCheck: RegistrationAndPropertyLinkCheckAction,
                                               findAPropertyConnector: FindAPropertyConnector,
                                               auditingService: AuditingService,
                                               findAPropertyRepo: FindAPropertyRepo,
                                               mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      Future.successful(Ok(manualPropertySearchView(form, createDefaultNavBar)))
    }

  def submit: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {
            val correctedFormErrors = formWithErrors.errors.map(formError =>
              if (formError.key.equals(""))
                formError.copy(key = "miniRateableValue")
              else
                formError
            )
            val formWithCorrectedErrors = formWithErrors.copy(errors = correctedFormErrors)
            Future.successful(BadRequest(manualPropertySearchView(formWithCorrectedErrors, createDefaultNavBar, true)))
          },
          manualPropertySearch => {
            auditingService.extendedAudit(ManualPropertySearchAuditModel(request.credId.getOrElse(""), manualPropertySearch, "results"),
              uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes.ManualPropertySearchController.show.url)
            findAPropertyConnector.findAPropertyManualSearch(manualPropertySearch).flatMap {
              case Left(error) =>
                Future.successful(Status(error.code)(Json.toJson(error)))
              case Right(properties) if properties.properties.isEmpty =>
                findAPropertyRepo.upsertProperty(LookUpVMVProperties(CredId(request.credId.getOrElse("")),properties))
                Future.successful(Redirect(routes.NoResultsFoundController.show.url))
              case Right(properties)  =>
                findAPropertyRepo.upsertProperty(LookUpVMVProperties(CredId(request.credId.getOrElse("")), properties))
                Future.successful(Redirect(routes.SingleSearchResultController.show(Some(1), Some("AddressASC")).url))
            }
          })
    }
}
