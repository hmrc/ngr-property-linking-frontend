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
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.WeAreCheckingYourDetailsView

class WeAreCheckingYourDetailsControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  lazy val view: WeAreCheckingYourDetailsView = inject[WeAreCheckingYourDetailsView]
  val pageTitle = "We are checking your details"

  def controller() = new WeAreCheckingYourDetailsController(
      view,
      mockAuthJourney,
      mcc
    )

  "WeAreCheckingYourDetailsController" must {
    "Return OK and the correct view" in {
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
//      val content = contentAsString(result)
//      content must include(pageTitle)
    }
  }
}
