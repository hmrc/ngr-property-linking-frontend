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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import org.scalatest.matchers.should.Matchers.shouldBe
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport

class SortingVMVPropertiesServiceSpec extends ControllerSpecSupport {

  def service = new SortingVMVPropertiesService()

  "SortingVMVPropertiesService" must {
    "Sort" must {
      "Sorting address ascending correctly" in {
        val actual = service.sort(properties4.properties, "AddressASC")
        actual.map(_.addressFull) shouldBe List("A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
          "M, RODLEY LANE, RODLEY, LEEDS, BH1 7EY", "Q, RODLEY LANE, RODLEY, LEEDS, BH1 7EY", "Z, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
      }

      "Sorting address descending correctly" in {
        val actual = service.sort(properties4.properties, "AddressDESC")
        actual.map(_.addressFull) shouldBe List("Z, RODLEY LANE, RODLEY, LEEDS, BH1 7EY", "Q, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
          "M, RODLEY LANE, RODLEY, LEEDS, BH1 7EY", "A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
      }

      "Sorting local authority reference ascending correctly" in {
        val actual = service.sort(properties4.properties, "ReferenceASC")
        actual.map(_.localAuthorityReference) shouldBe List("1191322564521", "2191322564521", "5191322564521", "9191322564521")
      }

      "Sorting local authority reference descending correctly" in {
        val actual = service.sort(properties4.properties, "ReferenceDESC")
        actual.map(_.localAuthorityReference) shouldBe List("9191322564521", "5191322564521", "2191322564521", "1191322564521")
      }

      "Sorting description text ascending correctly" in {
        val actual = service.sort(properties4.properties, "DescriptionASC")
        actual.map(_.valuations.last.descriptionText) shouldBe List("GOLF", "Lifeboat Station", "Miniature Railway", "SHOP AND PREMISES")
      }

      "Sorting description text descending correctly" in {
        val actual = service.sort(properties4.properties, "DescriptionDESC")
        actual.map(_.valuations.last.descriptionText) shouldBe List("SHOP AND PREMISES", "Miniature Railway", "Lifeboat Station","GOLF")
      }

      "Sorting rateable value ascending correctly" in {
        val actual = service.sort(properties4.properties, "RateableValueASC")
        actual.map(_.valuations.last.rateableValue.map(_.longValue).getOrElse(0l)) shouldBe List(0, 9300, 79300, 109300)
      }

      "Sorting rateable value descending correctly" in {
        val actual = service.sort(properties4.properties, "RateableValueDESC")
        actual.map(_.valuations.last.rateableValue.map(_.longValue).getOrElse(0l)) shouldBe List(109300, 79300, 9300, 0)
      }
    }
  }
}
