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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.Properties
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{AuthenticatedUserRequest, ErrorResponse}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.FindAPropertyView

import scala.concurrent.Future

class FindAPropertyControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val findAPropertyView: FindAPropertyView = inject[FindAPropertyView]
  val pageTitle = "Find a property"

  def controller() = new FindAPropertyController(
    findAPropertyView,
    mockFindAPropertyConnector,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(mockConfig)

  "FinAPropertyController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit valid postcode and redirect to no results found page" in {
        when(mockFindAPropertyConnector.findAProperty(any())(any())).thenReturn(Future.successful(Right(Properties(0, List.empty))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody(("postcode-value", "AA00 0AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino=true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.NoResultsFoundController.show.url)
      }

      "Successfully submit valid postcode and redirect to results page" in {
        when(mockFindAPropertyConnector.findAProperty(any())(any())).thenReturn(Future.successful(Right(Properties(0, List(testVmvProperty)))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody(("postcode-value", "AA00 0AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        //TODO: redirect to result page
        redirectLocation(result) mustBe Some(routes.AddPropertyToYourAccountController.show.url)
      }

      "Successfully submit valid postcode without space in between and redirect to no results found page" in {
        when(mockFindAPropertyConnector.findAProperty(any())(any())).thenReturn(Future.successful(Right(Properties(0, List.empty))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody(("postcode-value", "AA000AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.NoResultsFoundController.show.url)
      }

      "Successfully submit valid postcode  throws a BadRequestException" in {
        when(mockFindAPropertyConnector.findAProperty(any())(any())).thenReturn(Future(Left(ErrorResponse(400, "Bad request"))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody(("postcode-value", "AA000AA"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
      }

      "Submit with no postcode and display error message" in {
        when(mockFindAPropertyConnector.findAProperty(any())(any())).thenReturn(Future(Left(ErrorResponse(400, "Bad request"))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.FindAPropertyController.submit)
          .withFormUrlEncodedBody(("postcode-value", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
