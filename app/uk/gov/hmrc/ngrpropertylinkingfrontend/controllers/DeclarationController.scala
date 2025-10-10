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
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.UniqueIdGenerator
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.DeclarationView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeclarationController @Inject()(view: DeclarationView,
                                      authenticate: AuthRetrievals,
                                      isRegisteredCheck: RegistrationAction,
                                      propertyLinkingRepo: PropertyLinkingRepo,
                                      mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))
      propertyLinkingRepo.findByCredId(credId).map {
        case Some(answers) if answers.requestSentReference.isDefined =>
          Redirect(appConfig.ngrDashboardUrl)
        case _ =>
          Ok(view(createDefaultNavBar))
      }
    }
  
  def accept: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))
      val ref = UniqueIdGenerator.generateId

      propertyLinkingRepo.findByCredId(credId).flatMap {
        case Some(answers) if answers.requestSentReference.isDefined =>
          Future.successful(Redirect(appConfig.ngrDashboardUrl))

        case Some(_) =>
          propertyLinkingRepo.insertRequestSentReference(credId, ref).map {
            case Some(_) => Redirect(routes.AddPropertyRequestSentController.show)
            case None    => throw new Exception(s"Could not save reference for credId: ${credId.value}")
          }

        case None =>
          Future.failed(new NotFoundException(s"No property for credId: ${credId.value}"))
      }
    }
}

