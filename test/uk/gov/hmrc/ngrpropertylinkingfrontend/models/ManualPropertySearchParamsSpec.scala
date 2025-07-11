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

import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class ManualPropertySearchParamsSpec extends TestSupport {

  "ManualPropertySearchParams" should {

    "construct query string with only postcode" in {
      val params = ManualPropertySearchParams(postcode = "W1A 1AA")
      params.toQueryString mustBe "postcode=W1A+1AA"
      params.toUrl("https://lookup.service") mustBe "https://lookup.service/external-ndr-list-api/properties?postcode=W1A+1AA"
    }

    "include and clean optional fields" in {
      val params = ManualPropertySearchParams(
        postcode = "W1A 1AA",
        addressLine1 = Some("123 King's (Road)"),
        addressLine2 = Some("West End"),
        town = Some("London"),
        propertyReference = Some("ABC123"),
        miniRateableValue = Some(1000L),
        maxRateableValue = Some(2000L)
      )

      val query = params.toQueryString
      query must include("postcode=W1A+1AA")
      query must include("propertyNameNumber=123+Kings+Road")
      query must include("street=West+End")
      query must include("town=London")
      query must include("localAuthorityReference=ABC123")
      query must include("fromRateableValue=1000")
      query must include("toRateableValue=2000")

      val url = params.toUrl("https://lookup.service")
      url must startWith("https://lookup.service/external-ndr-list-api/properties?")
      url must include("postcode=W1A+1AA")
    }

    "URL encode values with spaces and symbols" in {
      val params = ManualPropertySearchParams(
        postcode = "AB1 2CD",
        addressLine1 = Some("Flat #3, (Old) House's")
      )

      val query = params.toQueryString
      query must include("postcode=AB1+2CD")
      query must include("propertyNameNumber=Flat+%233%2C+Old+Houses") // # encoded, cleaned
    }
  }
}
