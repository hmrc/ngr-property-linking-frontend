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

package uk.gov.hmrc.ngrpropertylinkingfrontend.helpers

import org.mockito.Mockito.when
import play.api.mvc._
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.auth.AuthJourney
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.auth.AuthenticatedUserRequest

import scala.concurrent.ExecutionContext

trait ControllerSpecSupport extends TestSupport {

  val mockAuthJourney: AuthJourney = mock[AuthJourney]
  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()
  val mockAppConfig: AppConfig = mock[AppConfig]
  mockRequest()

  def mockRequest(hasCredId: Boolean = false, hasNino: Boolean = true): Unit =
    when(mockAuthJourney.authWithUserDetails) thenReturn new ActionBuilder[AuthenticatedUserRequest, AnyContent] {
      override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => concurrent.Future[Result]): concurrent.Future[Result] = {
        val authRequest = AuthenticatedUserRequest(request, None, None, if (hasCredId) Some("1234") else None, None, None, nino = if (hasNino) Nino(hasNino = true, Some("AA000003D")) else Nino(hasNino = false, None))
        block(authRequest)
      }

      override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

      override protected def executionContext: ExecutionContext = ec
    }
}
