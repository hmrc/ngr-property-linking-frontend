package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import org.scalatestplus.play.PlaySpec

class UtilsSpec extends PlaySpec {

  "toLocalDate" when {
    "given a valid date string" should {
      "return a Some(LocalDate)" in {
        Utils.toLocalDate("2023-10-01") mustBe Some(java.time.LocalDate.of(2023, 10, 1))
      }
    }

    "given an invalid date string" should {
      "return None" in {
        Utils.toLocalDate("invalid-date") mustBe None
      }
    }

    "given an empty string" should {
      "return None" in {
        Utils.toLocalDate("") mustBe None
      }
    }
  }
}
