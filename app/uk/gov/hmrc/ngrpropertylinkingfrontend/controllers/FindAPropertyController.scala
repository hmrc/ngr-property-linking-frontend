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
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, PropertyLinkCheckAction, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.FindAPropertyConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.FindAProperty.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.LookUpVMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FindAPropertyRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.FindAPropertyView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FindAPropertyController @Inject()(findAPropertyView: FindAPropertyView,
                                        findAPropertyConnector: FindAPropertyConnector,
                                        authenticate: AuthRetrievals,
                                        isRegisteredCheck: RegistrationAction,
                                        isPropertyLinked: PropertyLinkCheckAction,
                                        mcc: MessagesControllerComponents,
                                        findAPropertyRepo: FindAPropertyRepo)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck andThen isPropertyLinked).async { implicit request =>
      Future.successful(Ok(findAPropertyView(form, createDefaultNavBar)))
    }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(findAPropertyView(formWithErrors, createDefaultNavBar))),
          findAProperty => {
            findAPropertyConnector.findAPropertyPostcodeSearch(findAProperty).flatMap {
              case Left(error) =>
                Future.successful(Status(error.code)(Json.toJson(error)))
              case Right(properties) if properties.properties.isEmpty =>
                findAPropertyRepo.upsertProperty(LookUpVMVProperties(CredId(request.credId.getOrElse("")),properties))
                Future.successful(Redirect(routes.NoResultsFoundController.show.url))
              case Right(properties) =>
                findAPropertyRepo.upsertProperty(LookUpVMVProperties(CredId(request.credId.getOrElse("")),properties))
                Future.successful(Redirect(routes.SingleSearchResultController.show(page = Some(1), sortBy = Some("AddressASC")).url))
            }
          })
    }
}
