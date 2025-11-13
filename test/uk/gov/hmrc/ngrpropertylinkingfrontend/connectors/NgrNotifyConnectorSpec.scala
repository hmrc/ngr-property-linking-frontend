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

package uk.gov.hmrc.ngrpropertylinkingfrontend.connectors

import play.api.http.Status.*
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrpropertylinkingfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.ErrorResponse
import scala.concurrent.Future

class NgrNotifyConnectorSpec extends MockHttpV2 {

  val connector = new NgrNotifyConnector(mockHttpClientV2, mockConfig)

  "Calling the property endpoint" when {

    "a valid property" should {
      "return a successful response from ngr-notify" in {
        val successResponse = HttpResponse(status = ACCEPTED, headers = Map.empty)
        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ngr-notify/property")(successResponse)
        val result: Future[Either[ErrorResponse, HttpResponse]] = connector.postProperty(testPropertyLinkingUserAnswers)
        result.futureValue mustBe Right(successResponse)
      }
    }

    "a ratepayer with missing data" should {
      "return a bad request response from ngr-notify" in {
        val badRequestResponse = HttpResponse(
          status = BAD_REQUEST,
          json = Json.obj("status" -> "BAD_REQUEST", "error" -> "Missing required field: uploadEvidence"),
          headers = Map.empty
        )
        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ngr-notify/property")(badRequestResponse)

        val result: Future[Either[ErrorResponse, HttpResponse]] = connector.postProperty(testPropertyLinkingUserAnswers)


        result.futureValue match {
          case Left(ErrorResponse(status, body)) =>
            status mustBe BAD_REQUEST
            Json.parse(body) mustBe badRequestResponse.json
          case _ =>
            fail("Expected Left(ErrorResponse) for bad request")
        }
      }
    }

    "an unexpected server response from ngr-notify" should {
      "return an internal server error from ngr-notify" in {
        val unexpectedResponse = HttpResponse(
          status = INTERNAL_SERVER_ERROR,
          body = "Server Error",
          headers = Map.empty
        )

        setupMockHttpV2Post(s"${mockConfig.ngrNotify}/ngr-notify/property")(unexpectedResponse)
        val result = connector.postProperty(testPropertyLinkingUserAnswers)
        result.futureValue match {
          case Left(ErrorResponse(status, body)) =>
            status mustBe INTERNAL_SERVER_ERROR
            body mustBe unexpectedResponse.body
          case _ =>
            fail("Expected Left(ErrorResponse) for an internal server error from ngr-notify")
        }
      }
    }

    "a failed call to ngr-notify ratepayer" should {
      "generate an internal server error response" in {
        setupMockHttpV2FailedPost(s"${mockConfig.ngrNotify}/ngr-notify/property")
        val result = connector.postProperty(testPropertyLinkingUserAnswers)
        result.futureValue match {
          case Left(ErrorResponse(status, body)) =>
            status mustBe INTERNAL_SERVER_ERROR
            body mustBe "Call to ngr-notify property endpoint failed: Request Failed"
          case _ =>
            fail("Expected Left(ErrorResponse) for a failed call to ngr-notify property endpoint")
        }
      }
    }
  }
}


