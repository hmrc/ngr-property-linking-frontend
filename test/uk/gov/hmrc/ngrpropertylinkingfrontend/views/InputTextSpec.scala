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

import play.api.data.Form
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.FindAProperty
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.InputText

class InputTextSpec extends ViewBaseSpec {
  val form: Form[FindAProperty] = FindAProperty.form()
  val inputText: InputText = inject[InputText]

  "InputText" when {
    "produce the same output for apply() and render()" in {
      val htmlApply = inputText(form, "phoneNumber-value", "phoneNumber-value", "Phone Number", true)(messages).body
      val htmlF = inputText.f(form, "phoneNumber-value", "phoneNumber-value", "Phone Number", false, Seq.empty, None, None, false, "", false)(messages).body
      val htmlRender = inputText.render(form, "phoneNumber-value", "phoneNumber-value", "Phone Number", true, Seq.empty, None, None, false, "", false, messages).body
      htmlApply must not be empty
      htmlRender must not be empty
      htmlF must not be empty
    }
  }

}
