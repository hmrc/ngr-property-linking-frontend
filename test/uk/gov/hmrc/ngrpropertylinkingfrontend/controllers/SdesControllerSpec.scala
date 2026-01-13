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

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import org.mockito.{ArgumentMatchers, Mockito}
import org.mockito.Mockito.{never, reset, times, verify}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.OptionValues.*
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Request, RequestHeader}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.{Property, SdesCallback}
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.SdesService

import java.time.LocalDateTime
import scala.concurrent.Future

final class SdesControllerSpec
  extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with GuiceOneAppPerSuite {

  private val mockService = mock[SdesService]
  private val mockAuth    = mock[AuthConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockService, mockAuth)
  }

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "play.http.filters" -> "play.api.http.NoHttpFilters",
        "auditing.enabled"  -> false,
        "metrics.enabled"   -> false
      )
      .overrides(
        bind[SdesService].toInstance(mockService),
        bind[AuthConnector].toInstance(mockAuth)
      )
      .build()

  implicit val propertyFormat: OFormat[Property]         = Json.format[Property]
  implicit val sdesCallbackFormat: OFormat[SdesCallback] = Json.format[SdesCallback]

  private val sample = SdesCallback(
    notification      = "FileReady",
    filename          = "example.pdf",
    correlationID     = "corr-123",
    dateTime          = LocalDateTime.of(2026, 1, 12, 10, 30, 0),
    checksumAlgorithm = Some("SHA-256"),
    checksum          = Some("0123456789abcdef"),
    availableUntil    = None,
    properties        = Nil,
    failureReason     = None
  )
  
  private val invalidJson = Json.obj(
    "notification" -> "FileReady",
    "filename"     -> "example.pdf"
  )

  private val callbackPath = "/ngr-property-linking-frontend/sdes-notification-callback"

  "SdesController.sdesCallback" - {

    "returns 202 Accepted when valid JSON and the service succeeds" in {
      Mockito.when(
        mockService.processCallback(ArgumentMatchers.any[SdesCallback])(
          ArgumentMatchers.any[HeaderCarrier],
          ArgumentMatchers.any[Request[?]]        // ← use RequestHeader if your signature uses it
        )
      ).thenReturn(Future.successful(()))

      val req = FakeRequest(POST, callbackPath)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(Json.toJson(sample))

      val resF = route(app, req).value
      status(resF) mustBe ACCEPTED

      verify(mockService, times(1))
        .processCallback(ArgumentMatchers.any[SdesCallback])(
          ArgumentMatchers.any[HeaderCarrier],
          ArgumentMatchers.any[Request[?]]
        )
    }

    "returns 400 BadRequest when JSON is invalid (and the service is not called)" in {
      val req = FakeRequest(POST, callbackPath)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(invalidJson)

      val resF = route(app, req).value
      status(resF) mustBe BAD_REQUEST

      verify(mockService, never())
        .processCallback(ArgumentMatchers.any[SdesCallback])(
          ArgumentMatchers.any[HeaderCarrier],
          ArgumentMatchers.any[Request[?]]
        )
    }

    "returns 500 InternalServerError when the service fails" in {
      Mockito.when(
        mockService.processCallback(ArgumentMatchers.any[SdesCallback])(
          ArgumentMatchers.any[HeaderCarrier],
          ArgumentMatchers.any[Request[?]]
        )
      ).thenReturn(Future.failed(new RuntimeException("boom")))

      val req = FakeRequest(POST, callbackPath)
        .withHeaders(CONTENT_TYPE -> JSON)
        .withBody(Json.toJson(sample))

      val resF = route(app, req).value
      status(resF) mustBe INTERNAL_SERVER_ERROR

      verify(mockService, times(1))
        .processCallback(ArgumentMatchers.any[SdesCallback])(
          ArgumentMatchers.any[HeaderCarrier],
          ArgumentMatchers.any[Request[?]]
        )
    }
  }
}

