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

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.VMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ManualPropertySearchView

import scala.concurrent.Future

class ManualPropertySearchControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val manualPropertySearchView: ManualPropertySearchView = inject[ManualPropertySearchView]
  val pageTitle = "What is the address?"

  def controller() = new ManualPropertySearchController(
    manualPropertySearchView,
    mockAuthJourney,
    mockMandatoryCheck,
    mockFindAPropertyConnector,
    mockFindAPropertyRepo,
    mcc
  )(mockConfig)

  "ManualPropertySearchController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit and redirect to no results found page" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAPropertyManualSearch(any())(any())).thenReturn(Future.successful(Right(VMVProperties(total = 0, List.empty))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody("addressLine1" -> "99",
            "town" -> "Worthing",
            "postcode" -> "W126WA")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.NoResultsFoundController.show.url)
      }

      "Successfully submit and redirect to properties found page" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAPropertyManualSearch(any())(any())).thenReturn(Future.successful(Right(properties11)))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody("addressLine1" -> "99",
            "town" -> "Worthing",
            "postcode" -> "W126WA")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SingleSearchResultController.show(Some(1), Some("AddressASC")).url)
      }

      "Submit with no postcode and display error message" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.ManualPropertySearchController.submit)
          .withFormUrlEncodedBody("addressLine1" -> "99",
            "town" -> "Worthing",
            "postcode" -> "")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("Enter postcode")
      }

      "Submit with invalid postcode and display error message" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.ManualPropertySearchController.submit)
          .withFormUrlEncodedBody("addressLine1" -> "99",
            "town" -> "Worthing",
            "postcode" -> "W12A6WA")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("Enter a full UK postcode")
      }
    }
  }
}
