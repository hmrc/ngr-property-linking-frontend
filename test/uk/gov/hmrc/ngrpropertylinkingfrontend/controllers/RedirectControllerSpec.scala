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

import org.mockito.Mockito.when
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport

class RedirectControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  val pageTitle = "Manage your business rates valuation"
  val expectedLogoutUrl = "http://localhost:1503/ngr-dashboard-frontend/signout"

  def controller() = new RedirectController(mockAuthJourney, mockIsRegisteredCheck, mcc)(mockConfig)

  "RedirectController" must {
    "redirect user to ngr dashboard signout" when {
      "logout() is called it" should {
        "return status code 303" in {
          val result = controller().signout()(authenticatedFakeRequest)
          status(result) mustBe SEE_OTHER
        }

        "return the ngr dashboard sign out url" in {
          val result = controller().signout()(authenticatedFakeRequest)
          redirectLocation(result) mustBe Some(expectedLogoutUrl)
        }
      }
    }

    "method dashboard" must {
      "Return SEE_OTHER and redirect to dashboard home page" in {
        val expectedUrl = "http://localhost:1503/ngr-dashboard-frontend/dashboard"
        val result = controller().dashboard()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(expectedUrl)
      }
    }
  }
}
