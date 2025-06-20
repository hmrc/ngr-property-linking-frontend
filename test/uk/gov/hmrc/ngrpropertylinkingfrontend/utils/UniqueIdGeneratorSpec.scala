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

package uk.gov.hmrc.ngrpropertylinkingfrontend.utils

import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class UniqueIdGeneratorSpec extends TestSupport {

  private val allowedChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

  "UniqueIdGenerator" must {

    "generate a 12 char id with 2 hyphens" in {
      val id = UniqueIdGenerator.generateId
      id.length mustBe 14
      val compactId = id.replace("-", "")
      compactId.length mustBe 12
      compactId.forall(allowedChars.contains(_)) mustBe true
    }

    "invalidate bad IDs" in {
      val invalidIds = List(
        "0FDE-DFD1-DGJ1",
        "0efkdkfvncma",
        "hello",
        "&fdh-9adf-4jnf"
      )

      invalidIds.foreach { id =>
        withClue(s"Expected '$id' to be invalid: ") {
          UniqueIdGenerator.validateId(id).isLeft mustBe true
        }
      }
    }

    "validate good IDs" in {
      val validIds = List(
        "fdfd-fdfd-dfdf",
        "VDJ4-5NSG-8RHW",
        "BDJ6867MLMNE",
        "nvjf5245bsmv"
      )

      validIds.foreach { id =>
        withClue(s"Expected '$id' to be valid: ") {
          UniqueIdGenerator.validateId(id).isRight mustBe true
        }
      }
    }
  }
}