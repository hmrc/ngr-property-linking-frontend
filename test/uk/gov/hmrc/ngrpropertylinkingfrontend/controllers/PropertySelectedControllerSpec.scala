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
import play.api.mvc.{AnyContentAsFormUrlEncoded, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{HeaderNames, NotFoundException}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.LookUpVMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{ErrorTemplate, PropertySelectedView}

import scala.concurrent.Future

class PropertySelectedControllerSpec extends ControllerSpecSupport {

  lazy val propertySelectedView: PropertySelectedView = inject[PropertySelectedView]
  lazy val errorView: ErrorTemplate = inject[ErrorTemplate]

  def controller() = new PropertySelectedController(
    propertySelectedView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc,
    mockFindAPropertyRepo,
    mockPropertyLinkingRepo
  )(mockConfig)

  "property selected controller" must {
    "method show" must {
      "Return Ok and the correct view with the property of index 0" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        val result = controller().show(index = 0)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
        content must include("SHOP AND PREMISES")
      }
      "Return Ok and the correct view with the property of index 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        val result = controller().show(index = 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("(INCL STORE R/O 5 & 5A) 5B, WEST LANE, WEST KEY, BOURNEMOUTH, BH1 7EY")
        content must include("SHOP AND PREMISES")
      }
      "Return NotFoundException when mongo fails to find property by credId" in{
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(None))
        val exception = intercept[NotFoundException] {
          await(controller().show(index = 1)(authenticatedFakeRequest))
        }
        exception.getMessage contains "Unable to find matching postcode" mustBe true
      }
    }
    "method submit" must {
      "Return See Other to the correct location when yes is selected" in {
        def requestWithFormValue(value: String): AuthenticatedUserRequest[AnyContentAsFormUrlEncoded] = AuthenticatedUserRequest(
          FakeRequest(
            routes.PropertySelectedController.submit(index = 0))
            .withFormUrlEncodedBody(("confirm-property-radio", value))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some("")))
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        when(mockPropertyLinkingRepo.upsertProperty(any())).thenReturn(Future.successful(true))
        mockRequest()
        val result = controller().submit(index = 0)(requestWithFormValue("Yes"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CurrentRatepayerController.show(mode = "").url)
      }
      "Return See Other to the correct location when no is selected" in {
        def requestWithFormValue(value: String): AuthenticatedUserRequest[AnyContentAsFormUrlEncoded] = AuthenticatedUserRequest(
          FakeRequest(
            routes.PropertySelectedController.submit(index = 0))
            .withFormUrlEncodedBody(("confirm-property-radio", value))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some("")))
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        mockRequest()
        val result = controller().submit(index = 0)(requestWithFormValue("no"))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SingleSearchResultController.show(1).url)
      }
      "Return form with errors" in {
        def requestWithFormValue(value: String): AuthenticatedUserRequest[AnyContentAsFormUrlEncoded] = AuthenticatedUserRequest(
          FakeRequest(
            routes.PropertySelectedController.submit(index = 0))
            .withFormUrlEncodedBody(("confirm-property-radio", value))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some("")))
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        mockRequest()
        val result = controller().submit(index = 0)(authenticatedFakeRequest)
        status(result) mustBe BAD_REQUEST
        val content = contentAsString(result)
        content must include("This field is required")
      }
      "Return NotFoundException when mongo fails to find property by credId" in {
        def requestWithFormValue(value: String): AuthenticatedUserRequest[AnyContentAsFormUrlEncoded] = AuthenticatedUserRequest(
          FakeRequest(
            routes.PropertySelectedController.submit(index = 0))
            .withFormUrlEncodedBody(("confirm-property-radio", value))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, None, None, None, nino = Nino(true, Some("")))

        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(None))
        when(mockPropertyLinkingRepo.upsertProperty(any())).thenReturn(Future.successful(true))
        mockRequest()
        val result = controller().submit(index = 0)(requestWithFormValue("Yes"))
        val exception = intercept[NotFoundException] {
          await(controller().submit(index = 0)(requestWithFormValue("Yes")))
        }
        exception.getMessage contains "No properties found on account" mustBe true
      }
    }
  }
}
