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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.CurrentRatepayerForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.DateTextFields

class DateTextFieldsSpec extends ViewBaseSpec {
  val form: Form[CurrentRatepayerForm] = CurrentRatepayerForm.form
  val dateTextFields: DateTextFields = inject[DateTextFields]

  val heading = "Enter the date you became the current ratepayer"
  val hint = "For example, 24 05 2026."
  val dayLabel = "Day"
  val monthLabel = "Month"
  val yearLabel = "Year"

  object Selectors {
    val heading = "div > fieldset > legend > h2"
    val hint = "#ratepayerDate-hint"
    val dayLabel = "#ratepayerDate > div:nth-child(1) > div > label"
    val monthLabel = "#ratepayerDate > div:nth-child(2) > div > label"
    val yearLabel = "#ratepayerDate > div:nth-child(3) > div > label"
  }


  "DateTextFields" when {
    val htmlApply = dateTextFields(form)(messages).body
    val htmlF = dateTextFields.f(form)(messages).body
    val htmlRender = dateTextFields.render(form, messages).body
    lazy implicit val document: Document = Jsoup.parse(htmlApply)

    "produce the same output for apply() and render()" in {
      htmlApply must not be empty
      htmlRender must not be empty
      htmlF must not be empty
    }

    "show correct heading" in {
      elementText(Selectors.heading) mustBe heading
    }

    "show correct hint" in {
      elementText(Selectors.hint) mustBe hint
    }

    "show correct day label" in {
      elementText(Selectors.dayLabel) mustBe dayLabel
    }

    "show correct month label" in {
      elementText(Selectors.monthLabel) mustBe monthLabel
    }

    "show correct year label" in {
      elementText(Selectors.yearLabel) mustBe yearLabel
    }
  }

}
