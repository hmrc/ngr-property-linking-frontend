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

class PropertySelectedFormSpec extends AnyWordSpec with Matchers {

  "PropertySelectedForm" should {

    "bind successfully with a valid confirmPropertyRadio value" in {
      val data = Map("confirm-property-radio" -> "Yes") // Use the correct key
      val boundForm = PropertySelectedForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(PropertySelectedForm("Yes"))
    }

    "fail to bind when confirmPropertyRadio is missing" in {
      val data = Map.empty[String, String]
      val boundForm = PropertySelectedForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("confirm-property-radio", List("propertySelected.error.required")))
    }

    "fail to bind when confirmPropertyRadio is empty" in {
      val data = Map("confirmpropertyradio" -> "")
      val boundForm = PropertySelectedForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("confirm-property-radio", List("propertySelected.error.required")))
    }


    "serialize to JSON correctly" in {
      val form = PropertySelectedForm("Yes")
      val json = Json.toJson(form)
      json shouldBe Json.obj(
        "radioValue" -> "Yes"
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("radioValue" -> "No")
      val result = json.validate[BusinessRatesBillForm]

      result.isSuccess shouldBe true
      result.get shouldBe BusinessRatesBillForm("No")
    }

    "fail deserialization if businessRatesBillRadio is missing" in {
      val json = Json.obj()
      val result = json.validate[BusinessRatesBillForm]

      result.isError shouldBe true
    }
  }
}
