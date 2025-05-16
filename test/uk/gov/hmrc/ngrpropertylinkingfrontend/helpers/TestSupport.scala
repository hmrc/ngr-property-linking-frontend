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

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrpropertylinkingfrontend.mock.MockAppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.auth.AuthenticatedUserRequest

import scala.concurrent.ExecutionContext

class TestSupport extends PlaySpec 
  with TestData
  with GuiceOneAppPerSuite
  with Matchers
  with MockitoSugar
  with Injecting
  with BeforeAndAfterEach
  with ScalaFutures
  with IntegrationPatience {
  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)
  lazy val mcc: MessagesControllerComponents = inject[MessagesControllerComponents]
  implicit lazy val ec: ExecutionContext = inject[ExecutionContext]

  lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("", "").withHeaders(HeaderNames.authorisation -> "Bearer 1")
  lazy val authenticatedFakeRequest: AuthenticatedUserRequest[AnyContentAsEmpty.type] =
    AuthenticatedUserRequest(fakeRequest, None, None, None, None, None, nino = Nino(true, Some("")))
}

