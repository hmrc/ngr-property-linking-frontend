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

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
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
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.BusinessRatesBillView

import java.time.LocalDate
import scala.concurrent.Future

class BusinessRatesBillControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val currentRatepayerView: BusinessRatesBillView = inject[BusinessRatesBillView]
  val pageTitle = "Do you have a business rates bill for the property? - GOV.UK"

  def controller() = new BusinessRatesBillController(
    currentRatepayerView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )(appConfig = mockConfig)

  "BusinessRatesBillController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty = testVmvProperty))))
        val result = controller().show("")(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Return OK with prepopulated data and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, businessRatesBill = Some("Yes")))))
        val result = controller().show("")(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        val document = Jsoup.parse(content)
        document.title() mustBe pageTitle
        document.select("input[value=Yes]").hasAttr("checked") shouldBe true
        document.select("input[value=No]").hasAttr("checked") shouldBe false
      }
    }

    "method submit" must {
      "Successfully submit when selected Yes and redirect to correct page" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId,vmvProperty = testVmvProperty))))
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty = testVmvProperty,currentRatepayer =  Some(CurrentRatepayer(true, None))))))
        val result = controller().submit("")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(""))
          .withFormUrlEncodedBody(("business-rates-bill-radio", "Yes"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.UploadBusinessRatesBillController.show(None).url)
      }

      "Successfully submit when selected No and redirect to correct page" in {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty =  testVmvProperty, currentRatepayer =  Some(CurrentRatepayer(false, Some("")))))))
        val result = controller().submit("")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(""))
          .withFormUrlEncodedBody(("business-rates-bill-radio", "No"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BusinessRatesBillController.show("").url)
      }

      "Successfully submit when use has come from cya page, selected No and redirect to correct page" in {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, currentRatepayer = Some(CurrentRatepayer(false, Some("")))))))
        val result = controller().submit("CYA")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(""))
          .withFormUrlEncodedBody(("business-rates-bill-radio", "No"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some(routes.CheckYourAnswersController.show.url)
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.BusinessRatesBillController.show("CYA").url)
      }

      "Submit with radio buttons unselected and display error message" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId,vmvProperty = testVmvProperty))))
        val result = controller().submit("")(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(""))
          .withFormUrlEncodedBody(("confirm-address-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}

