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


import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Nino
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.ReferenceType.NINO
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.*

import java.time.LocalDate

trait IntegrationTestData {
  val nino : Nino = Nino("AA000003D")
  
  val tokenAttributesResponseJson: String =
    """{
      | "authenticationProvider": "One Login",
      | "name": "John Ferguson",
      | "email": "test@testUser.com",
      | "identity": {
      |    "provider": "MDTP",
      |    "level": "50",
      |    "nino": "AB666666A"
      | },
      | "enrolments": [{
      |			"service": "IR-SA",
      |			"identifiers": [{
      |				"key": "UTR",
      |				"value": "1234567890"
      |			}],
      |   "state": "Activated",
      |			"friendlyName": "My SA"
      |		}],
      | "credId": "12345",
      | "eacdGroupId": "12345",
      | "caUserId": "12345"
      |}""".stripMargin



  val sampleRatepayerRegistration: RatepayerRegistration = RatepayerRegistration(
    userType = Some(UserType.Individual),
    agentStatus = Some(AgentStatus.Agent),
    name = Some(Name("Jane Doe")),
    tradingName = Some(TradingName("Jane's Bakery")),
    email = Some(Email("jane.doe@example.com")),
    nino = Some(Nino("AB123456C")),
    contactNumber = Some(PhoneNumber("07123456789")),
    secondaryNumber = Some(PhoneNumber("07987654321")),
    address = Some(Address("1 High Street", None, "London", None, Postcode("SW1A 1AA"))),
    trnReferenceNumber = Some(TRNReferenceNumber(NINO, "TRN123456")),
    isRegistered = Some(true)
  )

}
