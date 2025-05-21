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

import play.api.http.Status.OK
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.NoResultsFoundView

class NoResultsFoundControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val noResultsFoundView: NoResultsFoundView = inject[NoResultsFoundView]
  val pageTitle = "No results found - GOV.UK"

  def controller() = new NoResultsFoundController(
    noResultsFoundView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(mockConfig)

  "FinAPropertyController" must {
    val result = controller().show()(authenticatedFakeRequest)
    val content = contentAsString(result)
    "method show" must {
      "Return OK and the correct view" in {
        status(result) mustBe OK
        content must include(pageTitle)
      }
      "Return to account home link is presented" in {
        content must include("href=\"http://localhost:1503/ngr-dashboard-frontend/dashboard\"")
      }
      "Search again link is presented" in {
        content must include(routes.FindAPropertyController.show.url)
      }
    }
  }
}
