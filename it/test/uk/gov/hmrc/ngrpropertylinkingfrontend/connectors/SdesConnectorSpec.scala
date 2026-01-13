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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatest.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.SdesConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.WireMockHelper
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.PropertyExtractor.{formBundleKey, locationKey, mimeTypeKey, prefixedFormBundleKey}

import java.time.LocalDateTime
import scala.concurrent.Future

// imports for status codes
import play.api.http.Status.*

class SdesConnectorSpec
  extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with EitherValues
    with TryValues
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with WireMockHelper
    with ScalaCheckPropertyChecks {

  // Provide HeaderCarrier implicitly for the connector method call
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  val testCorrelationid = "testCorrelationid"
  val testNotificationType = "FileReceived"
  val testReference = "testReference1"
  val testReference2 = "testReference2"
  val testReference3 = "testReference3"
  val testDownloadUrl = "testDownloadUrl"
  val testFileName = "testFileName"
  val testMimeType = "testMimeType"
  val testTimeStamp: LocalDateTime = LocalDateTime.now()
  val testChecksum = "1234567890"
  val testSize = 123
  val testFormBundleId = "123412341234"
  val testInfoType = "1655996667080"
  val testRecipientOrSender = "400063095160"
  val testNrsId = "testNrsId"
  
  val sampleFileTransferNotification = FileTransferNotification(
    informationType = testInfoType,
    file = File(
      recipientOrSender = Some(testRecipientOrSender),
      name = s"$testFormBundleId-$testFileName",
      location = Some(testDownloadUrl),
      checksum = Checksum(
        algorithm = MD5,
        value = testChecksum
      ),
      size = testSize,
      properties = List(
        Property(
          name = locationKey,
          value = testDownloadUrl
        ),
        Property(
          name = mimeTypeKey,
          value = testMimeType
        ),
        Property(
          name = prefixedFormBundleKey,
          value = s"VRS$testFormBundleId"
        ),
        Property(
          name = formBundleKey,
          value = testFormBundleId
        )
      )
    ),
    audit = Audit(
      correlationID = testCorrelationid
    )
  )

  override lazy val app: Application = GuiceApplicationBuilder()
    .configure(
      "microservice.services.sdes.protocol" -> "http",
      "microservice.services.sdes.host"     -> "localhost",
      "microservice.services.sdes.port"     -> wireMockServer.port(),
      "sdes.authorization-token"            -> "test-token"
    )
    .build()

  private val sdesPath = "/sdes-stub/notification/fileready"

  private def stubResponse(path: String, status: Int): Unit =
    wireMockServer.stubFor(
      post(urlEqualTo(path))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("x-client-id", matching(".*"))
        .willReturn(aResponse().withStatus(status))
    )

  lazy val connector: SdesConnector = app.injector.instanceOf[SdesConnector]


  private val statusCases = Table(
    ("sdesStatusCode", "expectedConnectorResult"),
    (NO_CONTENT, Right(NO_CONTENT)),
    (BAD_REQUEST, Left(BAD_REQUEST)),
    (INTERNAL_SERVER_ERROR, Left(INTERNAL_SERVER_ERROR)),
    (REQUEST_TIMEOUT, Left(REQUEST_TIMEOUT))
  )
  
  "SDESConnector" - {
    forAll(statusCases) { (sdesStatusCode, expectedConnectorResult) =>
      s"sendFileNotification must return $expectedConnectorResult when SDES returns $sdesStatusCode" in {
        stubResponse(sdesPath, sdesStatusCode)
        val ftn: FileTransferNotification = sampleFileTransferNotification
        val result: Future[Either[Int, Int]] = connector.sendFileNotification(ftn)
        result.futureValue mustBe expectedConnectorResult
      }
    }
  }
}
