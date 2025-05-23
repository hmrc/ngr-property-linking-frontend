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

import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.WhatYouNeedView

class WhatYouNeedControllerSpec extends ControllerSpecSupport {
  val pageTitle = "What you need"
  val view: WhatYouNeedView = inject[WhatYouNeedView]
  val controller: WhatYouNeedController = new WhatYouNeedController(view, mockAuthJourney, mockIsRegisteredCheck, mcc)(mockConfig)

  "What you need controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller.show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Return OK and the correct view" in {
        val result = controller.next()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.FindAPropertyController.show.url)
      }
    }
  }
}
