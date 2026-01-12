/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.http.Status.NO_CONTENT
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport

class KeepAliveControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {

  def controller() = new KeepAliveController(mcc)

  "KeepAliveController.keepAlive" must {
    "return 204 NoContent" in {
      val result = controller().keepAlive()(fakeRequest)

      status(result) mustBe NO_CONTENT

      contentAsString(result) mustBe empty
    }
  }
}
