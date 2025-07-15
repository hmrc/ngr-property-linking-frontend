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

///*
// * Copyright 2025 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers
//
//import org.mockito.ArgumentMatchers.any
//import org.mockito.Mockito.when
//import org.scalatest.RecoverMethods.recoverToExceptionIf
//import org.scalatest.concurrent.ScalaFutures
//import play.api.http.Status.OK
//import play.api.test.Helpers.{contentAsString, status}
//import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
//import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{PropertyLinkingUserAnswers, UpscanRecord, UpscanReference}
//import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRatesBillView
//import play.api.test.Helpers.defaultAwaitTimeout
//import uk.gov.hmrc.http.NotFoundException
//import scala.concurrent.Future
//
//class UploadedBusinessRatesBillControllerSpec extends ControllerSpecSupport with ScalaFutures {
//  var view: UploadedBusinessRatesBillView = inject[UploadedBusinessRatesBillView]
//  def controller: UploadedBusinessRatesBillController = new UploadedBusinessRatesBillController(
//    view,
//    mockUpscanRepo,
//    mockAuthJourney,
//    mockIsRegisteredCheck,
//    mockPropertyLinkingRepo,
//    mcc
//  )
//
//  val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)
//
//  val reference: UpscanReference = UpscanReference("123456789")
//
//  val existingRecord: UpscanRecord = UpscanRecord(
//    credId = credId,
//    reference = reference,
//    status = "READY",
//    downloadUrl = None,
//    fileName = Some("FileName.jpg"),
//    failureReason = None,
//    failureMessage = None
//  )
//
//  val existingRecordWithFailure: UpscanRecord = UpscanRecord(
//    credId = credId,
//    reference = reference,
//    status = "UNKNOWN",
//    downloadUrl = None,
//    fileName = Some("FileName.jpg"),
//    failureReason = Some("QUARANTINE"),
//    failureMessage = None
//  )
//
//  "UploadedBusinessRatesBillController" must {
//    "Return OK and the correct view" in {
//      mockRequest(hasCredId = true)
//      when(mockUpscanRepo.findByCredId(any())).thenReturn(Future.successful(Some(existingRecord)))
//      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
//      val result = controller.show()(authenticatedFakeRequest)
//      status(result) mustBe OK
//      val content = contentAsString(result)
//      content must include("")
//    }
//
//    "Redirect when there is an error message" in {
//      mockRequest(hasCredId = true)
//      when(mockUpscanRepo.findByCredId(any())).thenReturn(Future.successful(Some(existingRecordWithFailure)))
//      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
//      val result = controller.show()(authenticatedFakeRequest)
//      status(result) mustBe 303
//    }
//
//    "Exception when no property returned" in {
//      mockRequest(hasCredId = true)
//      when(mockUpscanRepo.findByCredId(any())).thenReturn(Future.successful(Some(existingRecord)))
//      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
//      recoverToExceptionIf[NotFoundException] {
//        controller.show()(authenticatedFakeRequest)
//      }.map { ex =>
//        ex.getMessage mustBe "failed to find property from mongo"
//      }
//    }
//
//    "Exception when no upscan reference" in {
//      mockRequest(hasCredId = true)
//      when(mockUpscanRepo.findByCredId(any())).thenReturn(Future.successful(None))
//      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
//      recoverToExceptionIf[RuntimeException] {
//        controller.show()(authenticatedFakeRequest)
//      }.map { ex =>
//        ex.getMessage mustBe "No UpscanRecord found for credId: 1234"
//      }
//    }
//
//    "Exception when no credId in request" in {
//      when(mockUpscanRepo.findByCredId(any())).thenReturn(Future.successful(Some(existingRecord)))
//      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
//      recoverToExceptionIf[RuntimeException] {
//        controller.show()(authenticatedFakeRequest)
//      }.map { ex =>
//        ex.getMessage mustBe "No credId found in request"
//      }
//    }
//
//  }
//}
