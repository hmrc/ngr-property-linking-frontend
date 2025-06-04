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

  val testProperties: VMVProperties = VMVProperties(
    total = 1,
    properties = List(
      testVmvProperty,
    )
  )

  val testPropertyJsonResponse: JsValue = Json.parse(
    """ {
      |    "total": 1,
      |    "properties": [
      |      {
      |        "uarn": 11905603000,
      |        "addressFull": "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 1HU",
      |        "localAuthorityCode": "4720",
      |        "localAuthorityReference": "2191322564521",
      |        "valuations": [
      |          {
      |            "assessmentStatus": "CURRENT",
      |            "assessmentRef": 20351392000,
      |            "rateableValue": 7300,
      |            "scatCode": "249",
      |            "currentFromDate": "2019-02-12",
      |            "effectiveDate": "2018-04-01",
      |            "descriptionText": "SHOP AND PREMISES",
      |            "listYear": "2017",
      |            "primaryDescription": "CS",
      |            "allowedActions": [
      |              "challenge",
      |              "viewDetailedValuation",
      |              "propertyLink",
      |              "similarProperties"
      |            ],
      |            "propertyLinkEarliestStartDate": "2017-04-01",
      |            "listType": "previous"
      |          },
      |          {
      |            "assessmentStatus": "CURRENT",
      |            "assessmentRef": 25141561000,
      |            "rateableValue": 9300,
      |            "scatCode": "249",
      |            "currentFromDate": "2023-04-01",
      |            "effectiveDate": "2023-04-01",
      |            "descriptionText": "SHOP AND PREMISES",
      |            "listYear": "2023",
      |            "primaryDescription": "CS",
      |            "allowedActions": [
      |              "check",
      |              "challenge",
      |              "viewDetailedValuation",
      |              "propertyLink",
      |              "similarProperties"
      |            ],
      |            "propertyLinkEarliestStartDate": "2017-04-01",
      |            "listType": "current"
      |          },
      |          {
      |            "assessmentStatus": "CURRENT",
      |            "assessmentRef": 29775650000,
      |            "rateableValue": 9300,
      |            "scatCode": "249",
      |            "currentFromDate": "2026-04-01",
      |            "effectiveDate": "2026-04-01",
      |            "descriptionText": "SHOP AND PREMISES",
      |            "listYear": "2026",
      |            "primaryDescription": "CS",
      |            "allowedActions": [
      |              "viewDetailedValuation",
      |              "propertyLink",
      |              "similarProperties",
      |              "enquiry",
      |              "businessRatesEstimator"
      |            ],
      |           "propertyLinkEarliestStartDate": "2017-04-01",
      |           "listType": "current"
      |          }
      |        ]
      |      }
      |    ]
      |  }""".stripMargin)
  
  "Property" should {
    "deserialize to json" in {
      Json.toJson(testProperties) mustBe testPropertyJsonResponse
    }
    "serialize to json" in {
      testPropertyJsonResponse.as[VMVProperties] mustBe testProperties
    }
  }
}
