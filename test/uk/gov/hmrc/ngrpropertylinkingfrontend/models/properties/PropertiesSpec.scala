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
    """{
      |"total":1,
      |"properties":[
        |{
          |"localAuthorityReference":"2191322564521",
          |"valuations":[
          |{"descriptionText":"SHOP AND PREMISES",
          |"rateableValue":9300,
          |"assessmentRef":25141561000,
          |"scatCode":"249",
          |"currentFromDate":"2023-04-01",
          |"effectiveDate":"2023-04-01",
          |"listYear":"2023",
          |"propertyLinkEarliestStartDate":"2017-04-01",
          |"primaryDescription":"CS",
          |"listType":"current",
          |"assessmentStatus":"CURRENT",
          |"allowedActions":[
          |"check","challenge",
          |"viewDetailedValuation",
          |"propertyLink",
          |"similarProperties"
          |]
        |}
      |],
      |"addressFull":"(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
      |"localAuthorityCode":"4720",
      |"uarn":11905603000}]}""".stripMargin)

  "Property" should {
    "deserialize to json" in {
      Json.toJson(properties1) mustBe testPropertyJsonResponse
    }
    "serialize to json" in {
      testPropertyJsonResponse.as[VMVProperties] mustBe testProperties
    }
  }
}
