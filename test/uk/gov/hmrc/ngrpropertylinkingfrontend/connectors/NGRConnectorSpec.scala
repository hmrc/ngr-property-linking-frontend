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

import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrpropertylinkingfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.TRN

import scala.concurrent.Future

class NGRConnectorSpec extends MockHttpV2 {
  val ngrConnector: NGRConnector = new NGRConnector(mockHttpClientV2, mockConfig, mockNgrLogger)
  val email: Email = Email("hello@me.com")
  val trn: TRNReferenceNumber = TRNReferenceNumber(TRN, "1234")
  override lazy val credId: CredId = CredId("1234")

  "getRatepayer" when {
    "Successfully return a Ratepayer" in {
      val ratepayer: RatepayerRegistration = RatepayerRegistration()
      val response: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, Some(ratepayer))
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/get-ratepayer")(Some(response))
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue.get.credId mustBe credId
      result.futureValue.get.ratepayerRegistration mustBe Some(ratepayer)
    }
    "ratepayer not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/get-ratepayer")(None)
      val result: Future[Option[RatepayerRegistrationValuation]] = ngrConnector.getRatepayer(credId)
      result.futureValue mustBe None
    }
  }

  "getPropertyLinkingUserAnswers" when {
    "Successfully return a propertyLinkingUserAnswers" in {
      val response: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/get-property-linking-user-answers/${credId.value}")(Some(response))
      val result: Future[Option[PropertyLinkingUserAnswers]] = ngrConnector.getPropertyLinkingUserAnswers(credId)
      result.futureValue.get mustBe response
    }
    "ratepayer not found" in {
      setupMockHttpV2Get(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/get-property-linking-user-answers/${credId.value}")(None)
      val result: Future[Option[PropertyLinkingUserAnswers]] = ngrConnector.getPropertyLinkingUserAnswers(credId)
      result.futureValue mustBe None
    }
  }

  "upsertPropertyLinkingUserAnswers" when {
    "return HttpResponse when the response is 201 CREATED" in {
      val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)
      val response: HttpResponse = HttpResponse(201, "Created")
      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/upsert-property-linking-user-answers")(response)
      val result: Future[HttpResponse] = ngrConnector.upsertPropertyLinkingUserAnswers(propertyLinkingUserAnswers)
      result.futureValue.status mustBe 201
    }

    "throw an exception when response is not 201" in {
      val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)
      val response: HttpResponse = HttpResponse(400, "Bad Request")

      setupMockHttpV2Post(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/upsert-property-linking-user-answers")(response)

      val exception = intercept[Exception] {
        ngrConnector.upsertPropertyLinkingUserAnswers(propertyLinkingUserAnswers).futureValue
      }
      exception.getMessage must include("400: Bad Request")
    }

    "propagate exception when the request fails" in {
      val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)

      setupMockHttpV2FailedPost(s"${mockConfig.nextGenerationRatesHost}/next-generation-rates/upsert-property-linking-user-answers")
      val exception = intercept[RuntimeException] {
        ngrConnector.upsertPropertyLinkingUserAnswers(propertyLinkingUserAnswers).futureValue
      }
      exception.getMessage must include("Request Failed")
    }
  }
}


