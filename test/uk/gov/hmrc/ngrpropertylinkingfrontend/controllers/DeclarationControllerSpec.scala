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

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{await, contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.{AppConfig, FrontendAppConfig}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.DeclarationView

import scala.concurrent.Future

class DeclarationControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  lazy val view: DeclarationView = inject[DeclarationView]
  def controller() = new DeclarationController(
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )

  val appConfig: AppConfig = inject[FrontendAppConfig]
  val dashboard: String = appConfig.ngrDashboardUrl
  val referenceNumber: Option[String] =  Some("test-reqest-ref")

  "DeclarationController" must {

    "show" must {
      "redirect to dashboard if reference nr exists" in {
        when(mockPropertyLinkingRepo.findByCredId(any()))
          .thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(
            credId = credId,
            vmvProperty = testVmvProperty,
            requestSentReference = referenceNumber
          ))))

        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(dashboard)
      }

      "return OK and render declaration view if no reference nr" in {
        when(mockPropertyLinkingRepo.findByCredId(any()))
          .thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(
            credId = credId,
            vmvProperty = testVmvProperty,
            requestSentReference = None
          ))))

        val result = controller().show()(authenticatedFakeRequest)
        status(result) shouldBe OK
        contentAsString(result) must include("Declaration")
      }
    }

    "accept" must {
      "redirect to dashboard if reference already exists" in {
        when(mockPropertyLinkingRepo.findByCredId(any()))
          .thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(
            credId = credId,
            vmvProperty = testVmvProperty,
            requestSentReference = referenceNumber
          ))))

        val result = controller().accept()(authenticatedFakeRequest)
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(dashboard)
      }

      "insert reference and redirect to confirmation if no reference exists" in {
        when(mockPropertyLinkingRepo.findByCredId(any()))
          .thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(
            credId = credId,
            vmvProperty = testVmvProperty,
            requestSentReference = None
          ))))

        when(mockPropertyLinkingRepo.insertRequestSentReference(any(), any()))
          .thenReturn(Future.successful(Some(PropertyLinkingUserAnswers(
            credId = credId,
            vmvProperty = testVmvProperty,
            requestSentReference = referenceNumber
          ))))

        val result = controller().accept()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.AddPropertyRequestSentController.show.url)
      }

      "fail if no property found" in {
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))

        val result = controller().accept()(authenticatedFakeRequest)
        intercept[NotFoundException](await(result))
      }
    }
  }
}
