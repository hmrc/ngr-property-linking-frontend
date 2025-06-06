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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{AuthenticatedUserRequest, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{AddPropertyToYourAccountView, CurrentRatepayerView}

import scala.concurrent.Future

class CurrentRatepayerControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val currentRatepayerView: CurrentRatepayerView = inject[CurrentRatepayerView]
  val pageTitle = "When did you become the current ratepayer?"

  def controller() = new CurrentRatepayerController(
    currentRatepayerView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )(appConfig = mockConfig)

  "CurrentRatepayerController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Successfully submit when selected Before and redirect to correct page" in {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty = testVmvProperty,currentRatepayer =  Some("Before")))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit)
          .withFormUrlEncodedBody(("confirm-address-radio", "Before"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.WhatYouNeedController.show.url)
      }
      "Successfully submit when selected After and redirect to correct page" in {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null),vmvProperty =  testVmvProperty, currentRatepayer =  Some("After")))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit)
          .withFormUrlEncodedBody(("confirm-address-radio", "After"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.WhatYouNeedController.show.url)
      }
      "Submit with radio buttons unselected and display error message" in {
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit)
          .withFormUrlEncodedBody(("confirm-address-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}

