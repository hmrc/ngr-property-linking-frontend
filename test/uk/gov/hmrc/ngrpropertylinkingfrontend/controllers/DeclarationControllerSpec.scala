package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.DeclarationView

class DeclarationControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  lazy val view: DeclarationView = inject[DeclarationView]
  def controller() = new DeclarationController(
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )

  "DeclarationController" must {
    "Return OK and the correct view" in {
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include("Declaration")
    }
    
    "method accept" must {
      "Return OK and the correct view" in {
        val result = controller().accept()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddPropertyRequestSentController.show.url)
      }
    }

  }
}
