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
      boundForm.value shouldBe Some(CurrentRatepayerForm("Before", None, None, None))
    }

    "bind successfully with a valid current ratepayer radio value and invalid date" in {
      val data = Map("current-ratepayer-radio" -> "Before",
        "day" -> "AS",
        "month" -> "",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(CurrentRatepayerForm("Before", Some("AS"), None, Some("2026")))
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
        "day" -> "",
        "month" -> "4",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.empty.error")))
    }

    "fail to bind when current ratepayer month is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "1",
        "month" -> "",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.empty.error")))
    }

    "fail to bind when current ratepayer year is empty" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "1",
        "month" -> "4",
        "year" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.year.empty.error")))
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
        "day" -> "32",
        "month" -> "4",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.format.error"), ArraySeq(1, 31)))
    }

    "fail to bind when current ratepayer day has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "ABC",
        "month" -> "4",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.day.format.error"), ArraySeq("^[0-9]{1,2}$")))
    }

    "fail to bind when current ratepayer month is invalid" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "30",
        "month" -> "0",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.format.error"), ArraySeq(1, 12)))
    }

    "fail to bind when current ratepayer month has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "30",
        "month" -> "ABC",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.month.format.error"), ArraySeq("^[0-9]{1,2}$")))
    }

    "fail to bind when current ratepayer year has characters" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "30",
        "month" -> "12",
        "year" -> "ABC")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.year.format.error"), ArraySeq("^[0-9]{4}$")))
    }

    "fail to bind when current ratepayer date is invalid" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "30",
        "month" -> "2",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.date.format.error")))
    }

    "fail to bind when current ratepayer date is before 1 April 2026" in {
      val data = Map("current-ratepayer-radio" -> "After",
        "day" -> "31",
        "month" -> "3",
        "year" -> "2026")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("", List("currentRatepayer.date.invalid.error")))
    }

    "serialize to JSON correctly" in {
      val form = CurrentRatepayerForm("After", Some("1"), Some("4"), Some("2026"))
      val json = Json.toJson(form)

      json shouldBe Json.obj(
        "radioValue" -> "After",
        "day" -> "1",
        "month" -> "4",
        "year" -> "2026"
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("radioValue" -> "Before")
      val result = json.validate[CurrentRatepayerForm]

      result.isSuccess shouldBe true
      result.get shouldBe CurrentRatepayerForm("Before", None, None, None)
    }

    "deserialize from JSON correctly when selected After" in {
      val json = Json.obj("radioValue" -> "After",
        "day" -> "1",
        "month" -> "4",
        "year" -> "2026")
      val result = json.validate[CurrentRatepayerForm]

      result.isSuccess shouldBe true
      result.get shouldBe CurrentRatepayerForm("After", Some("1"), Some("4"), Some("2026"))
    }
    
    "fail deserialization if confirmAddressRadio is missing" in {
      val json = Json.obj()
      val result = json.validate[CurrentRatepayerForm]

      result.isError shouldBe true
    }
  }
}
