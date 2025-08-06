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
import org.scalatest.RecoverMethods.recoverToExceptionIf
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.*
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*
import uk.gov.hmrc.http.{NotFoundException, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadStatus.{Failed, InProgress, UploadedSuccessfully}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRateBillView

import scala.concurrent.Future

class UploadedBusinessRatesBillControllerSpec extends ControllerSpecSupport with ScalaFutures {
  var view: UploadedBusinessRateBillView = inject[UploadedBusinessRateBillView]

  def controller: UploadedBusinessRatesBillController = new UploadedBusinessRatesBillController(
    mockUploadProgressTracker,
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )

  val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)

  "UploadedBusinessRatesBillController" must {
    "Return OK and the correct view" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(Some(UploadedSuccessfully("filename.png", ".png", url"http://example.com/dummyLink", Some(120L)))))
      when(mockPropertyLinkingRepo.insertEvidenceDocument(any(), any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      val result = controller.show(UploadId("12345"), None)(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include("")
    }
    
    "Exception when no property returned" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
      recoverToExceptionIf[NotFoundException] {
        controller.show(UploadId("12345"), None)(authenticatedFakeRequest)
      }.map { ex =>
        ex.getMessage mustBe "Not found property on account"
      }
    }

    "Return Bad Request if there is no uploadId associated in the user session repository repo" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(None))
      val result = controller.show(UploadId("12345"), None)(authenticatedFakeRequest)
      status(result) mustBe BAD_REQUEST
    }

    "Exception when no credId in request" in {
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(Some(UploadedSuccessfully("filename.png", ".png", url"http://example.com/dummyLink", Some(120L)))))
      when(mockPropertyLinkingRepo.insertEvidenceDocument(any(), any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      recoverToExceptionIf[NotFoundException] {
        controller.show(UploadId("12345"), None)(authenticatedFakeRequest)
      }.map { ex =>
        ex.getMessage mustBe "No credId found in request"
      }
    }
  }
  
  "Calling the createSummary list function" should {
    "return the correct Summary list" when {
      "the Upload Status is set to inProgress" in {
        val result = controller.storeAndShowUploadProgress(credId, InProgress, None)
        val expected =
          SummaryList(List(
            SummaryListRow(
              key = Key(Text("Uploading")),
              value = Value(HtmlContent("""<span id="uploading-id"></span>"""))
            )
          ))
        result mustBe expected
      }

      "the Upload Status is set to Failed" in {
        val result = controller.storeAndShowUploadProgress(credId, Failed, None)
        val expected =
          SummaryList(List(
            SummaryListRow(
              key = Key(Text("Failed")),
              value = Value(HtmlContent("""<span id="failed-id"></span>"""))
            )
          ))
        result mustBe expected
      }
      
      "the Upload Status is to Successful" in {
        val result = controller.storeAndShowUploadProgress(credId, UploadedSuccessfully("filename.png", ".png", url"http://example.com/dummyLink", Some(120L)), None)
        val expected = SummaryList(List(
          SummaryListRow(Key(HtmlContent("""<a href="http://example.com/dummyLink" class="govuk-link govuk-summary-list__key_width">filename.png</a>"""), ""), Value(HtmlContent("""<span id="filename.png-id" class="govuk-tag govuk-tag--green">Uploaded</span>"""), ""), "", Some(Actions("", List(ActionItem("/ngr-property-linking-frontend/upload-business-rates-bill", Text("Remove"), None, "", Map("id" -> "remove-link"))))))), None, "", Map())

        result mustBe expected
      }
      
    }
  }
  
}
