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

class CurrentRatepayerFormSpec extends AnyWordSpec with Matchers {

  "CurrentRatepayerForm" should {

    "bind successfully with a valid confirmAddressRadio value" in {
      val data = Map("current-ratepayer-radio" -> "Before") // Use the correct key
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(CurrentRatepayerForm("Before"))
    }

    "fail to bind when confirmAddressRadio is missing" in {
      val data = Map.empty[String, String]
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("current-ratepayer-radio", List("error.required")))
    }

    "fail to bind when confirmAddressRadio is empty" in {
      val data = Map("currentratepayerradio" -> "")
      val boundForm = CurrentRatepayerForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("current-ratepayer-radio", List("error.required")))
    }


    "serialize to JSON correctly" in {
      val form = CurrentRatepayerForm("After")
      val json = Json.toJson(form)

      json shouldBe Json.obj(
        "radioValue" -> "After"
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("radioValue" -> "Before")
      val result = json.validate[CurrentRatepayerForm]

      result.isSuccess shouldBe true
      result.get shouldBe CurrentRatepayerForm("Before")
    }
    
    "fail deserialization if confirmAddressRadio is missing" in {
      val json = Json.obj()
      val result = json.validate[CurrentRatepayerForm]

      result.isError shouldBe true
    }
  }
}
