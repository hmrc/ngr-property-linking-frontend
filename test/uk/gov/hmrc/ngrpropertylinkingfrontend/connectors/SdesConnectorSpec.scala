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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.SdesConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestData
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.SdesNotifyStub.stubSdesNotification
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.PropertyExtractor.{checksumAlgorithm, locationKey, mimeTypeKey}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

// ==== Adjust these imports to your actual package locations ====
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*

// -----------------------------------------------------------------------------
// A tiny WireMock support trait providing start/stop/reset and the server ref.
// -----------------------------------------------------------------------------
//trait WireMockSupport {
//  lazy val wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())
//
//  def startWireMock(): Unit = wireMockServer.start()
//  def stopWireMock(): Unit  = wireMockServer.stop()
//  def resetWireMock(): Unit = wireMockServer.resetAll()
//}

// -----------------------------------------------------------------------------
// The actual test spec
// -----------------------------------------------------------------------------

//class SdesConnectorSpec
//  extends AnyWordSpec
//    with TestData
//    with Matchers
//    with ScalaFutures
//    with GuiceOneServerPerSuite
//    with TableDrivenPropertyChecks
//    with BeforeAndAfterAll
//    with BeforeAndAfterEach
//    with WireMockSupport {
//
//  lazy val connector: SdesConnector = app.injector.instanceOf[SdesConnector]
//
//  val testPayload: FileTransferNotification = testSdesPayload(testReference)
//  implicit val hc: HeaderCarrier = HeaderCarrier()
//  "notifySdes" when {
//    "SDES returns NO_CONTENT" must {
//      "return SdesNotificationSuccess" in {
//
//        stubSdesNotification(Json.toJson(testPayload))(NO_CONTENT)
//
//        val result = await(connector.notifySdes(testPayload))
//
//        result mustBe SdesNotificationSuccess(NO_CONTENT, "")
//      }
//    }
//
//    "SDES returns an unexpected response" must {
//      "return SdesNotificationFailure" in {
//
//        stubSdesNotification(Json.toJson(testPayload))(BAD_REQUEST)
//
//        val result = await(connector.notifySdes(testPayload))
//
//        result mustBe SdesNotificationFailure(BAD_REQUEST, "")
//      }
//    }
//  }
//}

