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

class ConnectionToPropertyFormSpec extends AnyWordSpec with Matchers {

  "ConnectionToPropertyForm" should {

    "bind successfully with a valid Connection to property value" in {
      val data = Map("connection-to-property-radio" -> "Owner") // Use the correct key
      val boundForm = ConnectionToPropertyForm.form().bind(data)

      boundForm.hasErrors shouldBe false
      boundForm.value shouldBe Some(ConnectionToPropertyForm.Owner)
    }

    "fail to bind when connectionToProperty is missing" in {
      val data = Map.empty[String, String]
      val boundForm = ConnectionToPropertyForm.form().bind(data)

      boundForm.hasErrors shouldBe true
      boundForm.errors should contain(FormError("connection-to-property-radio", List("connectionToProperty.radio.unselected.error")))
    }

    "return valid ConnectionToPropertyForm to the input string" in {
      ConnectionToPropertyForm.fromString("Owner") shouldBe ConnectionToPropertyForm.Owner
      ConnectionToPropertyForm.fromString("Occupier") shouldBe ConnectionToPropertyForm.Occupier
      ConnectionToPropertyForm.fromString("OwnerAndOccupier") shouldBe ConnectionToPropertyForm.OwnerAndOccupier
    }

    "throw IllegalArgumentException for invalid ConnectionToPropertyForm input" in {
      an[IllegalArgumentException] should be thrownBy ConnectionToPropertyForm.fromString("InvalidValue")
    }
  }
}
