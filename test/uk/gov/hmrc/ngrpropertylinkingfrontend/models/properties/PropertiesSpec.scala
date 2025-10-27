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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class PropertiesSpec extends TestSupport {

  val testProperties: VMVProperties = properties1

  val testPropertyJsonResponse: JsValue = Json.parse(
    """{"total":1,
      |"properties":[
      |{
      |"uarn":11905603000,
      |"addressFull":"(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
      |"localAuthorityCode":"4720",
      |"localAuthorityReference":"2191322564521",
      |"valuations":
      |[{"assessmentRef":25141561000,
      |"assessmentStatus":"CURRENT",
      |"rateableValue":9300,
      |"scatCode":"249",
      |"effectiveDate":"2023-04-01",
      |"currentFromDate":"2023-04-01",
      |"currentToDate":"2023-04-01",
      |"descriptionText":"SHOP AND PREMISES",
      |"listYear":"2023",
      |"primaryDescription":"CS",
      |"allowedActions":["check","challenge","viewDetailedValuation","propertyLink","similarProperties"],
      |"listType":"current"}]}],
      |"hasNext":true,
      |"hasPrevious":true}
      |""".stripMargin)

  "Property" should {
    "deserialize to json" in {
      println(Json.toJson(properties1))
      Json.toJson(properties1) mustBe testPropertyJsonResponse
    }
    "serialize to json" in {
      testPropertyJsonResponse.as[VMVProperties] mustBe testProperties
    }
  }
}
