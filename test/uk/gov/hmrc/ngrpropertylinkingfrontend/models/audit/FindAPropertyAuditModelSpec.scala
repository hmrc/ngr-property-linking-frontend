package uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit

import org.scalatest.wordspec.AnyWordSpecLike
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Postcode
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.FindAProperty

class FindAPropertyAuditModelSpec extends TestSupport with AnyWordSpecLike {

  "Find A Property Audit Model" should {

    val exampleModel = FindAPropertyAuditModel("123456", FindAProperty(Postcode("TQ59BW"), Some("10 Carnival Street")), "provide-your-national-insurance-number")

    "have the correct auditType when going to the national-insurance page" in {
      exampleModel.auditType mustBe "ngr-property-linking-frontend-provide-your-national-insurance-number"
    }

    "have the correct detail" in {
      val expectedDetail = Map("credId" -> "123456", "postcode" -> "TQ59BW", "propertyName" -> "10 Carnival Street")
      exampleModel.detail mustBe expectedDetail
    }
    "have the correct auditType when going to the check your answers page" in {
      val model = exampleModel.copy(nextPage = "check-answers")
      model.auditType mustBe "ngr-property-linking-frontend-check-answers"
    }
  }
}
