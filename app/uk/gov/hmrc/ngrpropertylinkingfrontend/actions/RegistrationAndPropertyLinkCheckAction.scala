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

package uk.gov.hmrc.ngrpropertylinkingfrontend.actions

import com.google.inject.ImplementedBy
import play.api.mvc.*
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationAndPropertyLinkCheckActionImpl @Inject()(
                                             ngrConnector: NGRConnector,
                                             propertyLinkingRepo: PropertyLinkingRepo,
                                             mcc: MessagesControllerComponents,
                                             authenticate: AuthRetrievals,
                                             appConfig: AppConfig
                                           )(implicit ec: ExecutionContext) extends RegistrationAndPropertyLinkCheckAction {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] = {
    authenticate.invokeBlock(request, { implicit authRequest: AuthenticatedUserRequest[A] =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(authRequest, authRequest.session)

      val credId = CredId(authRequest.credId.getOrElse(""))

      ngrConnector.getRatepayer(credId).flatMap { maybeRatepayer =>
        val isRegistered = maybeRatepayer
          .flatMap(_.ratepayerRegistration)
          .flatMap(_.isRegistered)
          .getOrElse(false)

        if (isRegistered) {
          checkPropertyLinkingReference(block)
        } else {
          redirectToLoginFrontend()
        }
      }
    })
  }

  private def checkPropertyLinkingReference[A](block: AuthenticatedUserRequest[A] => Future[Result])(implicit authRequest: AuthenticatedUserRequest[A], hc: HeaderCarrier): Future[Result] =
    propertyLinkingRepo.findByCredId(CredId(authRequest.credId.getOrElse(""))).flatMap {
      case Some(answers) if answers.requestSentReference.isDefined =>
        Future.successful(Redirect(appConfig.ngrCheckYourDetailsUrl))
      case _ =>
        ngrConnector.getPropertyLinkingUserAnswers(CredId(authRequest.credId.getOrElse(""))).flatMap { maybeUserAnswers =>
          if (maybeUserAnswers.flatMap(_.requestSentReference).isDefined)
            Future.successful(Redirect(appConfig.ngrCheckYourDetailsUrl))
          else
            block(authRequest)
        }
    }

  private def redirectToLoginFrontend(): Future[Result] = {
    Future.successful(Redirect(s"${appConfig.ngrLoginRegistrationHost}/ngr-login-register-frontend/register"))
  }



  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$
}

@ImplementedBy(classOf[RegistrationAndPropertyLinkCheckActionImpl])
trait RegistrationAndPropertyLinkCheckAction extends ActionBuilder[AuthenticatedUserRequest, AnyContent] with ActionFunction[Request, AuthenticatedUserRequest]
