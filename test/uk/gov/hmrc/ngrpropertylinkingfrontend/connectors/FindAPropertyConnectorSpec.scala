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

import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestData
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.Properties

import scala.concurrent.Future

class FindAPropertyConnectorSpec extends MockHttpV2 with TestData {

  val httpClientV2: HttpClientV2 = mock[HttpClientV2]
  val mockLogger: NGRLogger = mock[NGRLogger]
  val findAPropertyConnector: FindAPropertyConnector = new FindAPropertyConnector(mockHttpClientV2, mockConfig, mockLogger)


  "Calling the find a property api" when {
    "a valid postcode starts with LS1 is passed in" should {
      "return a 404(NOT FOUND)" in {
        val notFoundResponse = HttpResponse(status = NOT_FOUND, json = noResultsFoundJson, headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=LS1")(notFoundResponse)
        val result: Future[Either[ErrorResponse, Properties]] = findAPropertyConnector.findAProperty(testNoResultsFoundPostCode)
        result.futureValue mustBe Right(noResultsFoundProperty)
      }
      "return a 500" in {
        val internalServerErrorResponse = HttpResponse(status = INTERNAL_SERVER_ERROR, "Invalid postcode has been sent", headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=LS1")(internalServerErrorResponse)
        val result = findAPropertyConnector.findAProperty(testNoResultsFoundPostCode)
        result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR,"Invalid postcode has been sent"))
      }
    }
    "json is invalid" should {
      "return an error" in {
        val successResponse = HttpResponse(status = OK, json = Json.obj(), headers = Map.empty)
        setupMockHttpV2Get(s"${mockConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=LS1")(successResponse)
        val result = findAPropertyConnector.findAProperty(testNoResultsFoundPostCode)
        result.futureValue mustBe Left(ErrorResponse(BAD_REQUEST, "Json Validation Error: List((/properties,List(JsonValidationError(List(error.path.missing),ArraySeq()))), (/total,List(JsonValidationError(List(error.path.missing),ArraySeq()))))"))
      }
    }
    "the GET call has failed" in {
      setupMockFailedHttpV2Get(s"${mockConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=LS1")
      val result = findAPropertyConnector.findAProperty(testNoResultsFoundPostCode)
      result.futureValue mustBe Left(ErrorResponse(INTERNAL_SERVER_ERROR,"Call to VMV find a property failed"))
    }
  }
}
