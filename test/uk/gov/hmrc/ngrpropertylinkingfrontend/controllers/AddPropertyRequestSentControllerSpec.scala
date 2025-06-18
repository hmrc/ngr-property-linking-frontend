package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyRequestSentView

import scala.concurrent.Future

class AddPropertyRequestSentControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  lazy val view: AddPropertyRequestSentView = inject[AddPropertyRequestSentView]
  def controller() = new AddPropertyRequestSentController(
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc,
    mockPropertyLinkingRepo
  )

  "AddPropertyRequestSentController" must {

    "Return OK and the correct view" in {
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(credId = CredId(null), vmvProperty = testVmvProperty))))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include("Add a property request sent")
    }

  }
}
