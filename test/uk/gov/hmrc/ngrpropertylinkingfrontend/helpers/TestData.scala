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


import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{FeatureMap, HasGarage, Rooms, ScatCode}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.UserType.Individual
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.TRN

trait TestData {
  val testScatCode:ScatCode  = ScatCode("204")
  val credId: CredId = CredId("1234")
  val testAddress: Address =
    Address(
      line1 = "99",
      line2 = Some("Wibble Rd"),
      town = "Worthing",
      county = Some("West Sussex"),
      postcode = Postcode("BN110AA")
    )
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
}
