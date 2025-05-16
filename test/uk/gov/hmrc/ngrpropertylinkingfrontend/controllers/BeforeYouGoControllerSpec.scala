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

import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.ngrdashboardfrontend.config.AppConfig
import uk.gov.hmrc.ngrdashboardfrontend.views.html.BeforeYouGoView

class BeforeYouGoControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val beforeYouGoView: BeforeYouGoView = inject[BeforeYouGoView]
  val pageTitle = "Manage your business rates valuation"
 lazy val frontendAppConfig: AppConfig = inject[AppConfig]
  val expectedLogoutUrl = "http://localhost:9553/bas-gateway/sign-out-without-state?continue=http://localhost:1503/ngr-dashboard-frontend/beforeYouGo"

  def controller() = new BeforeYouGoController(beforeYouGoView, mcc)(appConfig = mockConfig)

  "BeforeYouGoController" must {
    "redirect user to before you go page" when {
      "logout() is called it" should {
        "return status code 303" in {
          val result = controller().signout()(authenticatedFakeRequest)
          status(result) mustBe SEE_OTHER
        }

        "return the bas gateway sign out url with before you go url" in {
          val result = controller().signout()(authenticatedFakeRequest)
          redirectLocation(result) mustBe Some(expectedLogoutUrl)
        }

        "new session contains no journeyId" in {
          val result = controller().signout()(authenticatedFakeRequest)
          result.map(result =>
            result.session.get("journeyId").isDefined mustBe false
          )
        }
      }
    }

    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
      "Return OK and sign back in link is presented" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("href=\"/ngr-dashboard-frontend/dashboard\"")
      }
      "Return OK and feedback survey link is presented" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("href=\"/ngr-dashboard-frontend/feedback\"")
      }
    }

    "method feedback" must {
      "Return SEE_OTHER and feedback Id is the session" in {
        val result = controller().feedback()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        result.map(result =>
          result.session.get("feedbackId").isDefined mustBe true
        )
      }
    }
  }
}
