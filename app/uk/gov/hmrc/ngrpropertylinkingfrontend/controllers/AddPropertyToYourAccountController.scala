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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAndPropertyLinkCheckAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit.{AuditModel, PropertySelectedAuditModel}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.AuditingService
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyToYourAccountView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddPropertyToYourAccountController @Inject()(
                                                    addPropertyToYourAccountView: AddPropertyToYourAccountView,
                                                    authenticate: AuthRetrievals,
                                                    mandatoryCheck: RegistrationAndPropertyLinkCheckAction,
                                                    auditingService: AuditingService,
                                                    mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {
  def show: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      Future.successful(Ok(addPropertyToYourAccountView(createDefaultNavBar)))
    }

  def submit: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      auditingService.extendedAudit(AuditModel(request.credId.value, "what-you-need"),
        uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes.AddPropertyToYourAccountController.show.url)
      Future.successful(Redirect(routes.WhatYouNeedController.show.url))
    }
}
