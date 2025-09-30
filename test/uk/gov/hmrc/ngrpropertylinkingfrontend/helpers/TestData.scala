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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.{VMVProperties, VMVProperty, Valuation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{PreparedUpload, Reference, UploadForm, UpscanInitiateRequest}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{FeatureMap, HasGarage, Rooms, ScatCode}

import java.time.{Instant, LocalDate}

trait TestData {

  val testPostcode: Postcode = Postcode(value = "BH1 7EY")
  val time: Instant = Instant.now()

  val properties1: VMVProperties = VMVProperties(total = 1,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
          )
        )
      )
    )
  )

  val properties4: VMVProperties = VMVProperties(total = 4,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "1191322564521",
        addressFull = "Q, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = None,
            scatCode = Some("249"),
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "Lifeboat Station",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1)),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "9191322564521",
        addressFull = "Z, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = Some(9300),
            scatCode = Some("249"),
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
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1)),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 85141561000L,
            rateableValue = Some(109300),
            scatCode = Some("249"),
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "GOLF",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1)),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "5191322564521",
        addressFull = "M, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 5141561000L,
            rateableValue = Some(79300),
            scatCode = Some("249"),
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "Miniature Railway",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1)),
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
            assessmentRef = 25141561000L,
            assessmentStatus = "CURRENT",
            rateableValue = Some(9300),
            scatCode = Some("249"),
            descriptionText = "SHOP AND PREMISES",
            effectiveDate = LocalDate.of(2023, 4, 1),
            currentFromDate = LocalDate.of(2023, 4, 1),
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            listType = "current",
            propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
        assessmentRef = 25141561000L,
        assessmentStatus = "CURRENT",
        rateableValue = Some(9300),
        scatCode = Some("249"),
        descriptionText = "SHOP AND PREMISES",
        effectiveDate = LocalDate.of(2023, 4, 1),
        currentFromDate = LocalDate.of(2023, 4, 1),
        listYear = "2023",
        primaryDescription = "CS",
        allowedActions = List(
          "check",
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        listType = "current",
        propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
      ),
      Valuation(
        assessmentRef = 25141561000L,
        assessmentStatus = "CURRENT",
        rateableValue = Some(9300),
        scatCode = Some("249"),
        descriptionText = "SHOP AND PREMISES",
        effectiveDate = LocalDate.of(2023, 4, 1),
        currentFromDate = LocalDate.of(2023, 4, 1),
        listYear = "2023",
        primaryDescription = "CS",
        allowedActions = List(
          "check",
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        listType = "current",
        propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
      ),
      Valuation(
        assessmentRef = 25141561000L,
        assessmentStatus = "CURRENT",
        rateableValue = Some(9300),
        scatCode = Some("249"),
        descriptionText = "SHOP AND PREMISES",
        effectiveDate = LocalDate.of(2023, 4, 1),
        currentFromDate = LocalDate.of(2023, 4, 1),
        listYear = "2023",
        primaryDescription = "CS",
        allowedActions = List(
          "check",
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        listType = "current",
        propertyLinkEarliestStartDate = Some(LocalDate.of(2017, 4, 1))
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
  
  val preparedUploadModel: PreparedUpload = PreparedUpload(Reference("ref"),UploadForm("href",Map("key" -> "value")))
  
  val preparedUploadJson: JsValue = Json.parse(
    """
      |{"reference":"ref","uploadRequest":{"href":"href","fields":{"key":"value"}}}
      |""".stripMargin)

  val upScanInitiateRequestModel: UpscanInitiateRequest =
    UpscanInitiateRequest("callBackUrl", Some("Success"), Some("error"), Some(10000), Some(25000000))

  val upscanInitiateRequestJson: JsValue = Json.parse(
    """
      |{
      |  "errorRedirect": "error",
      |  "callbackUrl": "callBackUrl",
      |  "maximumFileSize": 25000000,
      |  "minimumFileSize": 10000,
      |  "successRedirect": "Success"
      |}
      |""".stripMargin
  )

}
