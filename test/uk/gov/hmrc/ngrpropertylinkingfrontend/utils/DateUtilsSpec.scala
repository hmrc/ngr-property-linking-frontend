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

import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.DateUtils

class DateUtilsSpec extends PlaySpec {

  "toLocalDate" when {
    "given a valid date string" should {
      "return a Some(LocalDate)" in {
        DateUtils.toLocalDate("2023-10-01") mustBe Some(java.time.LocalDate.of(2023, 10, 1))
      }
    }

    "given an invalid date string" should {
      "return None" in {
        DateUtils.toLocalDate("invalid-date") mustBe None
      }
    }

    "given an empty string" should {
      "return None" in {
        DateUtils.toLocalDate("") mustBe None
      }
    }
  }
}
