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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Postcode

import scala.collection.immutable.ArraySeq

class ManualPropertySearchFormSpec extends AnyWordSpec with Matchers {

  "ManualPropertySearchForm" should {

    "bind successfully with valid manual property search values" in {
      val data = Map("addressLine1" -> "address line 1",
        "addressLine2" -> "",
        "town" -> "London",
        "county" -> "",
        "postcode" -> "BH1 6RE")
      val boundForm = ManualPropertySearchForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(ManualPropertySearchForm("address line 1", None, "London", None, Postcode("BH1 6RE")))
    }

    "fail to bind when manual property search is missing addressLine1, town and postcode" in {
      val data = Map("addressLine1" -> "",
        "addressLine2" -> "",
        "town" -> "",
        "county" -> "",
        "postcode" -> "")
      val boundForm = ManualPropertySearchForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("addressLine1", List("manualSearchProperty.line1.required.error"), ArraySeq("addressLine1")))
      boundForm.errors should contain(FormError("town", List("manualSearchProperty.city.required.error"), ArraySeq("town")))
      boundForm.errors should contain(FormError("postcode", List("manualSearchProperty.postcode.required.error"), ArraySeq("postcode")))
    }

    "fail to bind when manual property search has invalid mini and max rateable value" in {
      val data = Map("addressLine1" -> "address line 1",
        "addressLine2" -> "",
        "town" -> "London",
        "county" -> "",
        "postcode" -> "BH1 6RE",
        "miniRateableValue" -> "£,. ",
        "maxRateableValue" -> "£234,78ABC")
      val boundForm = ManualPropertySearchForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("miniRateableValue", List("manualSearchProperty.miniRateableValue.invalid.error"), ArraySeq("^(£{0,1}[0-9]{1,}((,|\\s|\\.){0,}[0-9]{0,})+)$")))
      boundForm.errors should contain(FormError("maxRateableValue", List("manualSearchProperty.maxRateableValue.invalid.error"), ArraySeq("^(£{0,1}[0-9]{1,}((,|\\s|\\.){0,}[0-9]{0,})+)$")))
    }

    "fail to bind when manual property search has mini and max rateable value over 20 characters" in {
      val data = Map("addressLine1" -> "address line 1",
        "addressLine2" -> "",
        "town" -> "London",
        "county" -> "",
        "postcode" -> "BH1 6RE",
        "miniRateableValue" -> "£2,147,483,647.0000000000000000000000000",
        "maxRateableValue" -> "£2,147,483,647.0000000000000000000000000")
      val boundForm = ManualPropertySearchForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("miniRateableValue", List("manualSearchProperty.miniRateableValue.maxLength.error"), ArraySeq(20)))
      boundForm.errors should contain(FormError("maxRateableValue", List("manualSearchProperty.maxRateableValue.maxLength.error"), ArraySeq(20)))
    }

    "fail to bind when manual property search has property preference over 100 characters" in {
      val data = Map("addressLine1" -> "address line 1",
        "addressLine2" -> "",
        "town" -> "London",
        "county" -> "",
        "postcode" -> "BH1 6RE",
        "propertyReference" -> "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789")
      val boundForm = ManualPropertySearchForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("propertyReference", List("manualSearchProperty.propertyReference.maxLength.error"), ArraySeq(100)))
    }
  }
}
