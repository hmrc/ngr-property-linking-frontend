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
import play.api.test.Helpers.{await, contentAsString, redirectLocation, status}
import play.api.test.{DefaultAwaitTimeout, FakeRequest}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{HeaderNames, NotFoundException}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{AuthenticatedUserRequest, CurrentRatepayer, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadEvidenceView
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.DateTextFields

import scala.concurrent.Future

class UploadEvidenceControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  val uploadEvidenceView: UploadEvidenceView = inject[UploadEvidenceView]
  val dateTextFields: DateTextFields = inject[DateTextFields]
  val pageTitle = "What evidence can you provide? - GOV.UK"

  def controller() = new UploadEvidenceController(
    uploadEvidenceView,
    mockAuthJourney,
    mockMandatoryCheck,
    mockAuditingService,
    mockPropertyLinkingRepo,
    mcc
  )(appConfig = mockConfig)

  "UploadEvidenceController" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty))))
        val result = controller().show(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Return OK with pre-populated the data, and the correct view" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty, uploadEvidence = Some("LandRegistry")))))
        val result = controller().show(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        val document = Jsoup.parse(content)
        document.title() mustBe pageTitle
        document.select("input[type=radio][name=upload-evidence-radio][value=Lease]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=upload-evidence-radio][value=LandRegistry]").hasAttr("checked") mustBe true
        document.select("input[type=radio][name=upload-evidence-radio][value=Licence]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=upload-evidence-radio][value=ServiceStatement]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=upload-evidence-radio][value=StampDuty]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=upload-evidence-radio][value=UtilityBill]").hasAttr("checked") mustBe false
        document.select("input[type=radio][name=upload-evidence-radio][value=WaterRate]").hasAttr("checked") mustBe false
      }

      "Throw exception when no property linking is found" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().show(authenticatedFakeRequest))
        }
        exception.getMessage contains "failed to find property from mongo" mustBe true
      }
    }

    "method submit" must {
      "Successfully submit when selected Land Registry title and redirect to correct page" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty))))
        when(mockPropertyLinkingRepo.insertUploadEvidence(any(), any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty, uploadEvidence = Some("LandRegistry")))))
        val result = controller().submit(AuthenticatedUserRequest(FakeRequest(routes.UploadEvidenceController.submit)
          .withFormUrlEncodedBody(("upload-evidence-radio", "LandRegistry"))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, credId = Some(credId.value), None, None, nino = Nino(true, Some(""))))
        result.map(result => {
          result.header.headers.get("Location") shouldBe Some("/ngr-login-register-frontend/upload-business-rates-bill")
        })
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.UploadBusinessRatesBillController.show(None, Some("LandRegistry")).url)
      }

      "Submit with radio buttons unselected and display error message" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty))))
        val result = controller().submit(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
          .withFormUrlEncodedBody(("upload-evidence-radio", ""))
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include(pageTitle)
      }

      "Submit with radio buttons unselected and credId is not found then throw exception" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
        mockRequest()
        val exception = intercept[NotFoundException] {
          await(controller().submit(AuthenticatedUserRequest(FakeRequest(routes.CurrentRatepayerController.submit(mode = ""))
            .withFormUrlEncodedBody(("upload-evidence-radio", ""))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some("")))))
        }
        exception.getMessage contains "failed to find property from mongo" mustBe true
      }
    }
  }
}

