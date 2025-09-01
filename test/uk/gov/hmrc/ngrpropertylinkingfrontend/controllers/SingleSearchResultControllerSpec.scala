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
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{await, contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.Nino
import uk.gov.hmrc.http.{BadRequestException, HeaderNames}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.LookUpVMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{ErrorTemplate, SingleSearchResultView}

import scala.concurrent.Future

class SingleSearchResultControllerSpec extends ControllerSpecSupport {

  val singleSearchResultView: SingleSearchResultView = inject[SingleSearchResultView]
  val errorView: ErrorTemplate = inject[ErrorTemplate]

  def controller() = new SingleSearchResultController(
    singleSearchResultView,
    errorView,
    mockAuthJourney,
    mockFindAPropertyRepo,
    mockIsRegisteredCheck,
    sortingVMVPropertiesService,
    mcc
  )(mockConfig)

  "single search result controller" must {
    "method show" must {
      "Return OK and the correct view when theirs 1 address on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties1))))
        val result = controller().show(Some(1), Some("AddressASC"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>1</strong> of <strong>1</strong> items.")
      }
      "Return OK and the correct view without given page nor sort option when theirs 1 address on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties1))))
        val result = controller().show(None, None)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>1</strong> of <strong>1</strong> items.")
      }
      "Return OK and the correct view with correct rateable value format" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        val result = controller().show(Some(1), Some("AddressASC"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("£9,300")
      }
      "Return Ok and the correct view with paginate on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
        val result = controller().show(Some(1), Some("AddressASC"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>10</strong> of <strong>11</strong> items.")
        content must include("Next")
      }
      "Return Ok and the correct view with paginate on page 2" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any())).thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties11))))
          .thenReturn(Future.successful(Right(properties11)))
        val result = controller().show(Some(2), Some("AddressASC"))(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>11</strong> to <strong>11</strong> of <strong>11</strong> items.")
        content must include("Previous")
      }
      "Redirect to no results found if mongo fails to find property by credId" in{
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any())).thenReturn(Future.successful(None))
          .thenReturn(Future.successful(Right(properties11)))
        val result = controller().show(Some(2), Some("AddressASC"))(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.FindAPropertyController.show.url)
      }
    }
    "method sort" must {
      "Return OK and the correct view when theirs 1 address on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId]))
          .thenReturn(Future.successful(Some(LookUpVMVProperties(credId, properties1))))
        val result = controller().sort(AuthenticatedUserRequest(FakeRequest(routes.SingleSearchResultController.sort)
          .withFormUrlEncodedBody("sortBy" -> "AddressASC")
          .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, emptyCredId, None, None, nino = Nino(hasNino = true, Some(""))))
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>1</strong> of <strong>1</strong> items.")
      }
      "Throw BadRequestException when form has error" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        val exception = intercept[BadRequestException] {
          await(controller().sort(authenticatedFakeRequest))
        }
        exception.getMessage contains "Unable to sort, please try again" mustBe true
      }
      "Throw BadRequestException when properties are not found in mongoDB" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyRepo.findByCredId(any[CredId])).thenReturn(Future.successful(None))
        val exception = intercept[BadRequestException] {
          await(controller().sort(AuthenticatedUserRequest(FakeRequest(routes.SingleSearchResultController.sort)
            .withFormUrlEncodedBody(("sortBy", "AddressASC"))
            .withHeaders(HeaderNames.authorisation -> "Bearer 1"), None, None, None, credId = credId.value, None, None, nino = Nino(true, Some("")))))
        }
        exception.getMessage contains "Unable to sort, please try again" mustBe true
      }
    }
    "method selectProperty" must {
      "Return SEE OTHER and pass chosen property index to confirm your address page with mode as check your answers" in {
        val result = controller().selectedProperty(1, "AddressASC")(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.PropertySelectedController.show(1, "AddressASC").url)
      }
    }
  }
}
