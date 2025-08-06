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

class UploadEvidenceFormSpec extends AnyWordSpec with Matchers {

  "UploadEvidenceForm" should {
    "bind successfully with a valid upload evidence value" in {
      val data = Map("upload-evidence-radio" -> "ServiceStatement") // Use the correct key
      val boundForm = UploadEvidenceForm.form.bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(UploadEvidenceForm("ServiceStatement"))
    }

    "fail to bind when upload evidence is missing" in {
      val data = Map.empty[String, String]
      val boundForm = UploadEvidenceForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("upload-evidence-radio", List("uploadEvidence.radio.unselected.error")))
    }

    "fail to bind when upload evidence radio is empty" in {
      val data = Map("upload-evidence-radio" -> "")
      val boundForm = UploadEvidenceForm.form.bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("upload-evidence-radio", List("uploadEvidence.radio.unselected.error")))
    }

    "serialize to JSON correctly" in {
      val form = UploadEvidenceForm("ServiceStatement")
      val json = Json.toJson(form)

      json shouldBe Json.obj(
        "radioValue" -> "ServiceStatement"
      )
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj("radioValue" -> "ServiceStatement")
      val result = json.validate[UploadEvidenceForm]

      result.isSuccess shouldBe true
      result.get shouldBe UploadEvidenceForm("ServiceStatement")
    }

    "fail deserialization if upload evidence radio is missing" in {
      val json = Json.obj()
      val result = json.validate[UploadEvidenceForm]

      result.isError shouldBe true
    }
  }
}
