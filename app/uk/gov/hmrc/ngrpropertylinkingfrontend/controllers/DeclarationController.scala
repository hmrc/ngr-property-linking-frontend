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
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.NgrNotifyConnector
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
                                      mandatoryCheck: RegistrationAndPropertyLinkCheckAction,
                                      propertyLinkingRepo: PropertyLinkingRepo,
                                      ngrConnector: NGRConnector,
                                      ngrNotifyConnector: NgrNotifyConnector,
                                      mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      Future.successful(Ok(view(createDefaultNavBar)))
    }

  def accept: Action[AnyContent] =
    (authenticate andThen mandatoryCheck ).async { implicit request =>
      val ref = UniqueIdGenerator.generateId
      val credId = CredId(request.credId.getOrElse(""))
      for {
        maybeAnswers <- propertyLinkingRepo.insertRequestSentReference(credId, ref)
        userAnswers <- maybeAnswers match {
          case Some(result) => Future.successful(result)
          case None => Future.failed(new Exception(s"Could not save reference for credId: ${credId.value}"))
        }
        ngrConnectorResponse <- ngrConnector.upsertPropertyLinkingUserAnswers(userAnswers)
        ngrNotifyConnectorResponse <- ngrNotifyConnector.postProperty(userAnswers)

        result <- (ngrNotifyConnectorResponse, ngrConnectorResponse.status) match {
          case (Right(resp), CREATED) if resp.status == ACCEPTED  =>
            Future.successful(Redirect(routes.AddPropertyRequestSentController.show))
          case (Right(resp), status) if status != CREATED =>
            Future.failed(new Exception(s"Failed upsert to backend for credId: ${credId.value}"))
          case (Left(error), _) =>
            Future.failed(new Exception(s"Failed call to ngr-notify property endpoint for credId: ${credId.value}"))
          case _ =>
            Future.failed(new Exception(s"Unknown failure for credId: ${credId.value}"))
        }
      } yield result
    }
}

