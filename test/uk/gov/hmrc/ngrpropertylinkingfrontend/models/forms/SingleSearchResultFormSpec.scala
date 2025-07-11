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
import play.api.libs.json.Json

class SingleSearchResultFormSpec extends AnyWordSpec with Matchers {

  "SingleSearchResultForm" should {

    "bind successfully" in {
      val data = Map("sortBy" -> "AddressASC")
      val boundForm = SingleSearchResultForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(SingleSearchResultForm("AddressASC"))
    }

    "serialize to JSON correctly" in {
      val form = SingleSearchResultForm("AddressASC")
      val json = Json.toJson(form)

      json shouldBe Json.obj("sortBy" -> "AddressASC")
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("sortBy" -> "AddressASC")
      val result = json.validate[SingleSearchResultForm]

      result.isSuccess shouldBe true
      result.get shouldBe SingleSearchResultForm("AddressASC")
    }
  }
}
