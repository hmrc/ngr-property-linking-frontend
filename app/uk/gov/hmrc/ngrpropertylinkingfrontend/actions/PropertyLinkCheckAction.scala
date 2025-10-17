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
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertyLinkCheckActionImpl @Inject()(
                                             propertyLinkingRepo: PropertyLinkingRepo,
                                             mcc: MessagesControllerComponents,
                                             authenticate: AuthRetrievals,
                                             appConfig: AppConfig
                                           )(implicit ec: ExecutionContext) extends PropertyLinkCheckAction {

  override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] = {
    authenticate.invokeBlock(request, { implicit authRequest: AuthenticatedUserRequest[A] =>
      
      propertyLinkingRepo.findByCredId(CredId(authRequest.credId.getOrElse(""))).flatMap {
        case Some(answers) if answers.requestSentReference.isDefined =>
          Future.successful(Redirect(appConfig.ngrDashboardUrl))
        case _ =>
          block(authRequest)
      }
    })
  }

  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser
  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$
}

@ImplementedBy(classOf[PropertyLinkCheckActionImpl])
trait PropertyLinkCheckAction extends ActionBuilder[AuthenticatedUserRequest, AnyContent] with ActionFunction[Request, AuthenticatedUserRequest]
