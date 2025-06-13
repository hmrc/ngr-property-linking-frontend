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

package uk.gov.hmrc.ngrpropertylinkingfrontend.helpers


import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.{VMVProperties, VMVProperty, Valuation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{FeatureMap, HasGarage, Rooms, ScatCode}

import java.time.{Instant, LocalDate}

trait TestData {

  val testPostcode = Postcode(value = "BH1 7EY")
  val time = Instant.now()

  val properties1: VMVProperties = VMVProperties(total = 1,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      )
    )
  )

  val properties11: VMVProperties = VMVProperties(total = 11,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 5 & 5A) 5B, WEST LANE, WEST KEY, BOURNEMOUTH, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      )
    )
  )
  
  val testScatCode:ScatCode  = ScatCode(204)
  lazy val credId: CredId = CredId("1234")
  val testAddress: Address =
    Address(
      line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA")
    )
  val testVmvProperty: VMVProperty = VMVProperty(
    localAuthorityReference = "2191322564521",
    uarn = 11905603000L,
    addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 1HU",
    localAuthorityCode = "4720",
    valuations = List(
      Valuation(
        assessmentStatus = "CURRENT",
        assessmentRef = 20351392000L,
        rateableValue = 7300,
        scatCode = "249",
        currentFromDate = LocalDate.of(2019, 2, 12),
        effectiveDate = LocalDate.of(2018, 4, 1),
        descriptionText = "SHOP AND PREMISES",
        listYear = "2017",
        primaryDescription = "CS",
        allowedActions = List(
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
        listType = "previous"
      ),
      Valuation(
        assessmentStatus = "CURRENT",
        assessmentRef = 25141561000L,
        rateableValue = 9300,
        scatCode = "249",
        currentFromDate = LocalDate.of(2023, 4, 1),
        effectiveDate = LocalDate.of(2023, 4, 1),
        descriptionText = "SHOP AND PREMISES",
        listYear = "2023",
        primaryDescription = "CS",
        allowedActions = List(
          "check",
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
        listType = "current"
      ),
      Valuation(
        assessmentStatus = "CURRENT",
        assessmentRef = 29775650000L,
        rateableValue = 9300,
        scatCode = "249",
        currentFromDate = LocalDate.of(2026, 4, 1),
        effectiveDate = LocalDate.of(2026, 4, 1),
        descriptionText = "SHOP AND PREMISES",
        listYear = "2026",
        primaryDescription = "CS",
        allowedActions = List(
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties",
          "enquiry",
          "businessRatesEstimator"
        ),
        propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
        listType = "current"
      ),
    ))
  val testFeatureMap: FeatureMap =

    FeatureMap.empty
    .add(HasGarage, true)
    .add(Rooms, 10)

  val testRegistrationModel: RatepayerRegistration = RatepayerRegistration(
          userType = Some(Individual),
          agentStatus = Some(AgentStatus.Agent),
          name = Some(Name("John Doe")),
          tradingName = Some(TradingName("CompanyLTD")),
          email = Some(Email("JohnDoe@digital.hmrc.gov.uk")),
          contactNumber = Some(PhoneNumber("07123456789")),
          secondaryNumber = Some(PhoneNumber("07123456789")),
          address = Some(
          Address(line1 = "99",
            line2 = Some("Wibble Rd"),
            town = "Worthing",
            county = Some("West Sussex"),
            postcode = Postcode("BN110AA")
          )
        ),
        trnReferenceNumber = Some(TRNReferenceNumber(TRN, "12345")),
        isRegistered = Some(false)
      )

  val regResponseJson: JsValue = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"tradingName":{"value":"CompanyLTD"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"secondaryNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"}},"trnReferenceNumber":{"referenceType":"TRN","value":"12345"},"isRegistered":false}
      |""".stripMargin)


  val minRegResponseJson: JsValue = Json.parse(
    """{"userType":"Individual","agentStatus":"Agent","name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"contactNumber":{"value":"07123456789"},"address":{"line1":"99","line2":"Wibble Rd","town":"Worthing","county":"West Sussex","postcode":{"value":"BN110AA"}},"trnReferenceNumber":{"referenceType":"TRN","value":"12345"},"isRegistered":false}
      |""".stripMargin)

  val minRegResponseModel: RatepayerRegistration = testRegistrationModel.copy(tradingName = None, secondaryNumber = None)

  val regValuationModel: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId = credId, Some(testRegistrationModel))
  val regValuationJson: JsValue = Json.parse(
    """
      |{"credId":{"value":"1234"},"ratepayerRegistration":{"name":{"value":"John Doe"},"email":{"value":"JohnDoe@digital.hmrc.gov.uk"},"secondaryNumber":{"value":"07123456789"},"agentStatus":"Agent","trnReferenceNumber":{"referenceType":"TRN","value":"12345"},"userType":"Individual","contactNumber":{"value":"07123456789"},"address":{"postcode":{"value":"BN110AA"},"line1":"99","county":"West Sussex","line2":"Wibble Rd","town":"Worthing"},"tradingName":{"value":"CompanyLTD"},"isRegistered":false}}
      |""".stripMargin
  )

  val minRegValuationModel: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId = credId, None)
  val minRegValuationJson: JsValue = Json.parse(
    """
      |{"credId":{"value":"1234"}}
      |""".stripMargin
  )

  val noResultsFoundJson: JsValue = Json.parse(
    """
      |{
      |    "total": 0,
      |    "properties": []
      |  }
      |""".stripMargin
  )

  val noResultsFoundProperty: VMVProperties = VMVProperties(0, List.empty)

}
