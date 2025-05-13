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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyStatus.ActionNeeded

class PropertySpec extends TestSupport {

  val testProperty: Property = Property(
    scatCode = testScatCode,
    address = testAddress,
    status =  ActionNeeded,
    features =  testFeatureMap
  )

  val testPropertyJsonResponse: JsValue = Json.parse(
    """{"scatCode":{"value":"204"},
      |"address":{"postcode":{"value":"BN110AA"}},
      |"status":"ActionNeeded",
      |"features":{"HasGarage":true, "Rooms":10}}""".stripMargin)
  
  "Property" should {
    "deserialize to json" in {
      Json.toJson(testProperty) mustBe testPropertyJsonResponse
    }
    "serialize to json" in {
      testPropertyJsonResponse.as[Property] mustBe testProperty
    }
    "get value of feature from key" in{
      testProperty.features.get(Rooms) mustBe Some(10)
    }
    
  }
}
