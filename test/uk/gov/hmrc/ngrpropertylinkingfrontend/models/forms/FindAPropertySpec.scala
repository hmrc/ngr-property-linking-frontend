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
import play.api.libs.json.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Postcode


class FindAPropertySpec extends AnyWordSpec with Matchers {

  "FindAProperty" should {

    "serialize to JSON correctly" in {
      val model = FindAProperty(Postcode("AB12CD"), Some("Test Property"))
      val expectedJson = Json.obj(
        "postcode" -> Json.obj("value" -> "AB12CD"),
        "propertyName" -> "Test Property"
      )

      Json.toJson(model) shouldBe expectedJson
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "postcode" -> Json.obj("value" -> "AB12CD"),
        "propertyName" -> "Test Property"
      )

      val result = json.validate[FindAProperty]

      result.isSuccess shouldBe true
      result.get shouldBe FindAProperty(Postcode("AB12CD"), Some("Test Property"))
    }

    "handle missing optional propertyName on deserialization" in {
      val json = Json.obj(
        "postcode" -> Json.obj("value" -> "AB12CD")
      )

      val result = json.validate[FindAProperty]

      result.isSuccess shouldBe true
      result.get shouldBe FindAProperty(Postcode("AB12CD"), None)
    }

    "produce the correct string representation with propertyName" in {
      val model = FindAProperty(Postcode("AB12CD"), Some("Test Property"))
      model.toString shouldBe "Some(Test Property),AB12CD"
    }

    "produce the correct string representation with no propertyName" in {
      val model = FindAProperty(Postcode("AB12CD"), None)
      model.toString shouldBe "None,AB12CD"
    }
  }
}
