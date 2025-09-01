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
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{AuthenticatedUserRequest, CurrentRatepayer, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ConnectionToPropertyView

import java.time.LocalDate
import scala.concurrent.Future

class ConnectionToPropertyControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {

  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val connectionToPropertyView: ConnectionToPropertyView = inject[ConnectionToPropertyView]
  val pageTitle = "What is your connection to the property? - GOV.UK"

  def controller() = new ConnectionToPropertyController(
    connectionToPropertyView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc,
    mockPropertyLinkingRepo,
  )(appConfig = mockConfig, ec = ec)

  "ConnectionToPropertyController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Return OK with pre-populated data and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, connectionToProperty = Some("Owner")))))
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        val document = Jsoup.parse(content)
        document.title() mustBe pageTitle
        document.select("input[type=radio][name=connection-to-property-radio][value=Owner]").hasAttr("checked") mustBe true
        document.select("input[type=radio][name=connection-to-property-radio][value=Occupier]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=connection-to-property-radio][value=OwnerAndOccupier]").hasAttr("checked") mustBe false
      }
    }

    "method submit" must {
      "Successfully submit when selected Before and redirect to correct page" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty))))
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, currentRatepayer = Some(CurrentRatepayer(true, None)), connectionToProperty = Some("Owner")))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.ConnectionToPropertyController.submit)
          .withFormUrlEncodedBody((ConnectionToPropertyForm.formName, "Owner"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, emptyCredId, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") mustBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.show.url)
      }
      "Successfully submit when selected After and redirect to correct page" in {
        when(mockPropertyLinkingRepo.insertCurrentRatepayer(any(), any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, currentRatepayer = Some(CurrentRatepayer(false, Some(""))), connectionToProperty = Some("Occupier")))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.ConnectionToPropertyController.submit)
          .withFormUrlEncodedBody((ConnectionToPropertyForm.formName, "Occupier"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, emptyCredId, None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") mustBe Some("/ngr-login-register-frontend/confirm-your-contact-details")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.show.url)
      }
      "Submit with radio buttons unselected and display error message" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty))))
        val result = controller().submit()(AuthenticatedUserRequest(FakeRequest(routes.ConnectionToPropertyController.submit)
          .withFormUrlEncodedBody((ConnectionToPropertyForm.formName, ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, emptyCredId, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

  }



}
