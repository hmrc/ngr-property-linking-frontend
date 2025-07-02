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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.data.FormError
import play.api.libs.json.Json

import scala.collection.immutable.ArraySeq

class CurrentRatepayerFormSpec extends AnyWordSpec with Matchers {

  "CurrentRatepayerForm" should {

    "bind successfully with a valid current ratepayer radio value" in {
      val data = Map("current-ratepayer-radio" -> "Before") // Use the correct key
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(CurrentRatepayerForm("Before", None))
    }

    //TODO: change ignore back to in on or after 1 April 2026
    //This test can only pass on or after 1 April 2026 due to the rule of become ratepayer date
    //has to fall between 1 April 2026 and today.
    //Below test will automatically use today's date as become ratepayer date.
    "bind successfully with current ratepayer radio value as After and valid date" ignore {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "1",
        "ratepayerDate.month" -> "4",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(CurrentRatepayerForm("After", Some(RatepayerDate("1", "4", "2026"))))
    }

    "bind successfully with a valid current ratepayer radio value and invalid date" in {
      val data = Map("current-ratepayer-radio" -> "Before",
        "ratepayerDate.day" -> "AS",
        "ratepayerDate.month" -> "",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(CurrentRatepayerForm("Before", Some(RatepayerDate("AS", "", "2026"))))
    }

    "fail to bind when current ratepayer radio is missing" in {
      val data = Map.empty[String, String]
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("current-ratepayer-radio", List("currentRatepayer.radio.unselected.error")))
    }

    "fail to bind when current ratepayer radio is empty" in {
      val data = Map("currentratepayerradio" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("current-ratepayer-radio", List("currentRatepayer.radio.unselected.error")))
    }

    "fail to bind when current ratepayer day is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "",
        "ratepayerDate.month" -> "4",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.empty.error")))
    }

    "fail to bind when current ratepayer month is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "1",
        "ratepayerDate.month" -> "",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.empty.error")))
    }

    "fail to bind when current ratepayer year is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "1",
        "ratepayerDate.month" -> "4",
        "ratepayerDate.year" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.year.empty.error")))
    }

    "fail to bind when current ratepayer day and month are empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "",
        "ratepayerDate.month" -> "",
        "ratepayerDate.year" -> "2025")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.month.empty.error")))
    }

    "fail to bind when current ratepayer day and year are empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "",
        "ratepayerDate.month" -> "4",
        "ratepayerDate.year" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.year.empty.error")))
    }

    "fail to bind when current ratepayer month and year are empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "31",
        "ratepayerDate.month" -> "",
        "ratepayerDate.year" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.year.empty.error")))
    }

    "fail to bind when current ratepayer date is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "",
        "month" -> "",
        "year" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("" ,List("currentRatepayer.date.empty.error")))
    }

    "fail to bind when current ratepayer day is invalid" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "32",
        "ratepayerDate.month" -> "13",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.format.error"), ArraySeq(1, 31)))
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.format.error"), ArraySeq(1, 12)))
    }

    "fail to bind when current ratepayer day has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "ABC",
        "ratepayerDate.month" -> "4",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.format.error"), ArraySeq("^[0-9]{1,2}$")))
    }

    "fail to bind when current ratepayer month is invalid" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "30",
        "ratepayerDate.month" -> "0",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.format.error"), ArraySeq(1, 12)))
    }

    "fail to bind when current ratepayer month has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "30",
        "ratepayerDate.month" -> "ABC",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.format.error"), ArraySeq("^[0-9]{1,2}$")))
    }

    "fail to bind when current ratepayer year has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "30",
        "ratepayerDate.month" -> "12",
        "ratepayerDate.year" -> "ABC")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.year.format.error"), ArraySeq("^[0-9]{4}$")))
    }

    "fail to bind when current ratepayer date is invalid" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "30",
        "ratepayerDate.month" -> "2",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.date.format.error")))
    }

    "fail to bind when current ratepayer date is before 1 April 2026" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "ratepayerDate.day" -> "31",
        "ratepayerDate.month" -> "3",
        "ratepayerDate.year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.date.invalid.error")))
    }

    "serialize to JSON correctly" in {
      val form = CurrentRatepayerForm("After", Some(RatepayerDate("1", "4", "2026")))
      val json = Json.toJson(form)

      json shouldBe Json.obj(
        "radioValue" -> "After",
        "maybeRatepayerDate" -> Json.obj(
          "day" -> "1",
          "month" -> "4",
          "year" -> "2026"
        )
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("radioValue" -> "Before")
      val result = json.validate[CurrentRatepayerForm]

      result.isSuccess shouldBe true
      result.get shouldBe CurrentRatepayerForm("Before", None)
    }

    "deserialize from JSON correctly when selected After" in {
      val json = Json.obj("radioValue" -> "After",
        "maybeRatepayerDate" -> Json.obj(
          "day" -> "1",
          "month" -> "4",
          "year" -> "2026"
        )
      )
      val result = json.validate[CurrentRatepayerForm]

      result.isSuccess shouldBe true
      result.get shouldBe CurrentRatepayerForm("After", Some(RatepayerDate("1", "4", "2026")))
    }

    "fail deserialization if confirmAddressRadio is missing" in {
      val json = Json.obj()
      val result = json.validate[CurrentRatepayerForm]

      result.isError shouldBe true
    }
  }
}
