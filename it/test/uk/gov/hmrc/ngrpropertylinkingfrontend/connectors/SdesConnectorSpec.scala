/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.connectors

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{IntegrationSpecBase, TestData}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.SdesNotifyStub.stubSdesNotification
import uk.gov.hmrc.ngrpropertylinkingfrontend.stubs.{AuthStub, SdesStubs}

// ==== Adjust these imports to your actual package locations ====
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*

class SdesConnectorSpec
  extends AnyWordSpec
    with IntegrationSpecBase
    with TestData
    with Matchers
    with ScalaFutures
    with GuiceOneServerPerSuite
    with TableDrivenPropertyChecks
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  lazy val connector: SdesConnector = app.injector.instanceOf[SdesConnector]

  val testPayload: FileTransferNotification = testSdesPayload(testReference)


  "notifySdes" when {

    "class SdesConnectorSpec" when {
      "SDES returns NO_CONTENT" must {
        "return SdesNotificationSuccess" in {
          AuthStub.authorised
          SdesStubs.fileReadyStub

          stubSdesNotification(Json.toJson(testPayload))(NO_CONTENT)


          println("App SDES URL: " + app.injector.instanceOf[AppConfig].sdesNotificationUrl)
          println("WireMock unmatched: " + wireMockServer.findUnmatchedRequests().getRequests)

          wireMockServer.getAllServeEvents.forEach(println)


          val result = await(connector.notifySdes(testPayload))

          result mustBe SdesNotificationSuccess(NO_CONTENT, "")
        }
      }


      "SDES returns OK" must {
        "return SdesNotificationSuccess if we allow 200" in {
          SdesStubs.stubSdesNotification(Json.toJson(testPayload))(OK)


          val result = await(connector.notifySdes(testPayload))

          result mustBe SdesNotificationSuccess(OK, "")
        }
      }
    }

  }
}

