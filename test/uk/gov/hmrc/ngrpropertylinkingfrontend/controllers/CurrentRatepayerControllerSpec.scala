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

import org.junit.Ignore
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{AuthenticatedUserRequest, CurrentRatepayer, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.DateTextFields
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{AddPropertyToYourAccountView, CurrentRatepayerView}

import java.time.LocalDate
import scala.concurrent.Future

class CurrentRatepayerControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val currentRatepayerView: CurrentRatepayerView = inject[CurrentRatepayerView]
  lazy val dateTextFields: DateTextFields = inject[DateTextFields]
  val pageTitle = "When did you become the current ratepayer?"

  def controller() = new CurrentRatepayerController(
    currentRatepayerView,
    dateTextFields,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )(appConfig = mockConfig)

  "CurrentRatepayerController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty = testVmvProperty))))
        val result = controller().show(mode = "")(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit when selected Before and redirect to correct page" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId,vmvProperty = testVmvProperty))))
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty = testVmvProperty,currentRatepayer =  Some(CurrentRatepayer("Before", None))))))
        val result = controller().submit(mode = "")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody(("current-ratepayer-radio", "Before"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BusinessRatesBillController.show("").url)
      }

      //When on and after 1 April 2026 is selected, the date must be between 1 April 2026 and today.
      //As we are still in 2025, this test will always fail on validation.
      //Ignored this test for now till we reach 1 April 2026
      "Successfully submit when selected After and redirect to correct page" ignore {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty =  testVmvProperty, currentRatepayer =  Some(CurrentRatepayer("After", Some(LocalDate.now())))))))
        val result = controller().submit(mode = "")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody(("current-ratepayer-radio", "After"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BusinessRatesBillController.show("").url)
      }

      "Submit with radio buttons unselected and display error message" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId,vmvProperty = testVmvProperty))))
        val result = controller().submit(mode = "")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody(("current-ratepayer-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Submit when selected After and date is before 1 April 2026" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId,vmvProperty = testVmvProperty))))
        val result = controller().submit(mode = "")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody("current-ratepayer-radio" -> "After",
            "day" -> "31",
            "month" -> "12",
            "year" -> "2025")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("The date you became the current ratepayer must be between 1 April 2026 and today")
      }

      "Submit when selected After and date is after today" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty))))
        val date = LocalDate.now().plusDays(7)
        val result = controller().submit(mode = "")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody("current-ratepayer-radio" -> "After",
            "day" -> date.getDayOfMonth.toString,
            "month" -> date.getMonthValue.toString,
            "year" -> date.getYear.toString)
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("The date you became the current ratepayer must be between 1 April 2026 and today")
      }
    }
  }
}

