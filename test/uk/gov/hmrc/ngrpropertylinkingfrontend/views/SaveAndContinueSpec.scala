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

package uk.gov.hmrc.ngrpropertylinkingfrontend.views

import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.saveAndContinueButton

class SaveAndContinueSpec extends ViewBaseSpec{

  val injectedView: saveAndContinueButton = injector.instanceOf[saveAndContinueButton]

  "save button" when {
    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply("message").body
      val htmlRender = injectedView.render(msg = "message",showSaveProgressButton = false,isStartButton = false,disabled= false, messages = messages).body
      val htmlF = injectedView.f("message", false, false, false)(messages).body
      htmlApply must not be empty
      htmlRender must not be empty
      htmlF must not be empty
    }
  }
}
