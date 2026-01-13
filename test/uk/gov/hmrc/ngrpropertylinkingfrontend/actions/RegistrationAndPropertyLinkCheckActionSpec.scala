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

package uk.gov.hmrc.ngrpropertylinkingfrontend.actions

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{spy, when}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers.{OK, SEE_OTHER, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.VMVProperty
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.{CredId, RatepayerRegistrationValuation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo

import scala.concurrent.Future

class RegistrationAndPropertyLinkCheckActionSpec extends TestSupport with TestData {


  override implicit lazy val app: Application = GuiceApplicationBuilder().build()

  private val mockNGRConnector: NGRConnector = mock[NGRConnector]
  private val mockPropertyLinkingRepo: PropertyLinkingRepo = mock[PropertyLinkingRepo]
  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  private val mockAuthAction = new AuthRetrievalsImpl(mockAuthConnector, mcc)

  private val action = new RegistrationAndPropertyLinkCheckActionImpl(
    ngrConnector = mockNGRConnector,
    propertyLinkingRepo = mockPropertyLinkingRepo,
    mcc = mcc,
    authenticate = mockAuthAction,
    appConfig = mockConfig
  )
  private class Stubs {
    def successBlock(request: Request[AnyContent]): Future[Result] = Future.successful(Ok(""))
  }

  private implicit class HelperOps[A](a: A) {
    def ~[B](b: B) = new~(a, b)
  }

  private val testPropertyLinkingUserAnswers: PropertyLinkingUserAnswers =
    PropertyLinkingUserAnswers(
      credId = CredId(testCredId.providerId),
      vmvProperty = properties1.properties.head,
      requestSentReference = None
    )

  private val retrievalResult: Future[mockAuthAction.RetrievalsType] =
    Future.successful(
      Some(testCredId) ~
        Some(testNino) ~
        testConfidenceLevel ~
        Some(testEmail) ~
        Some(testAffinityGroup) ~
        Some(testName)
    )
   private  val stubs = spy(new Stubs)

  "PropertyLinkCheckAction" when {

    "property is already linked with the user" must {
      "redirect to the Add Property Request Sent page" in {
        when(mockAuthConnector.authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any())).thenReturn(retrievalResult)
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(isRegistered = Some(true)))))))
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(testPropertyLinkingUserAnswers.copy(requestSentReference = Some("ABC123")))))

        val result = action.invokeBlock(fakeRequest, stubs.successBlock)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(mockConfig.ngrCheckYourDetailsUrl)
      }

      "redirect to the Add Property Request Sent page when only find propertyLinkingUserAnswers in backend mongoDB" in {
        when(mockAuthConnector.authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any())).thenReturn(retrievalResult)
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(isRegistered = Some(true)))))))
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
        when(mockNGRConnector.getPropertyLinkingUserAnswers()(any())).thenReturn(Future.successful(Some(testPropertyLinkingUserAnswers.copy(requestSentReference = Some("ABC123")))))

        val result = action.invokeBlock(fakeRequest, stubs.successBlock)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(mockConfig.ngrCheckYourDetailsUrl)
      }
    }

    "property is not yet linked" must {
      "allow the request to proceed without redirect" in {
        when(mockAuthConnector.authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any()))
          .thenReturn(retrievalResult)
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel.copy(isRegistered = Some(true)))))))
        val notLinkedAnswers = testPropertyLinkingUserAnswers.copy(requestSentReference = None)
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(notLinkedAnswers)))
        when(mockNGRConnector.getPropertyLinkingUserAnswers()(any())).thenReturn(Future.successful(Some(notLinkedAnswers)))

        val result = action.invokeBlock(fakeRequest, stubs.successBlock)
        status(result) mustBe OK
      }
      "redirect to dashboard when the user is not registered" in {
        when(mockAuthConnector.authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any()))
          .thenReturn(retrievalResult)
        when(mockNGRConnector.getRatepayer(any())(any()))
          .thenReturn(Future.successful(Some(RatepayerRegistrationValuation(credId, Some(testRegistrationModel)))))
        
        val result = action.invokeBlock(fakeRequest, stubs.successBlock)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(s"${mockConfig.ngrLoginRegistrationHost}/ngr-login-register-frontend/register")
      }
      "missing credentials must throw an exception" in {
        when(
          mockAuthConnector
            .authorise[mockAuthAction.RetrievalsType](any(), any())(any(), any())
        ).thenReturn(Future.successful(None ~ Some(testNino) ~ testConfidenceLevel ~ Some(testEmail) ~ Some(testAffinityGroup) ~ Some(testName)))

        val result = action.invokeBlock(fakeRequest, stubs.successBlock)

        whenReady(result.failed) { e =>
          e.getMessage mustBe "User credentials are missing"
        }
      }
    }
  }
}




