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
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.NoResultsFoundView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class NoResultsFoundController @Inject()(noResultsFoundView: NoResultsFoundView,
                                         authenticate: AuthRetrievals,
                                         isRegisteredCheck: RegistrationAction,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport {
  def show(): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      Future.successful(Ok(noResultsFoundView(createDefaultNavBar, routes.FindAPropertyController.show().url, appConfig.ngrDashboardUrl)))
    }
}
