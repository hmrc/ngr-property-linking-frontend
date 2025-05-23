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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class PropertiesSpec extends TestSupport {

  val testProperties: Properties = Properties(
    total = 100,
    properties = List(testVmvProperty, testVmvProperty, testVmvProperty)
  )

  val testPropertyJsonResponse: JsValue = Json.parse(
    """{"total": 100,
      |"properties":[
      |{"uarn": 11905603000, "addressFull":"(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, LS13 1HU"},
      |{"uarn": 11905603000, "addressFull":"(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, LS13 1HU"},
      |{"uarn": 11905603000, "addressFull":"(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, LS13 1HU"}
      |]
      |}""".stripMargin)
  
  "Property" should {
    "deserialize to json" in {
      Json.toJson(testProperties) mustBe testPropertyJsonResponse
    }
    "serialize to json" in {
      testPropertyJsonResponse.as[Properties] mustBe testProperties
    }
  }
}
