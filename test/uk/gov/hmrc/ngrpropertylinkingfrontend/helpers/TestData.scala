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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{FeatureMap, HasGarage, Rooms, ScatCode}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.TRN
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.{Properties, VMVProperty}

trait TestData {
  val testScatCode:ScatCode  = ScatCode("204")
  lazy val credId: CredId = CredId("1234")
  val testAddress: Address =
    Address(
      line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA")
    )
  val testVmvProperty: VMVProperty = VMVProperty(11905603000l, "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, LS13 1HU")
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

  val noResultsFoundProperty: Properties = Properties(0, List.empty)

}
