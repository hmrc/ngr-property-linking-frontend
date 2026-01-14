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
import play.api.mvc.Call
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*
import uk.gov.hmrc.http.{NotFoundException, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadStatus.UploadedSuccessfully
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRateBillView

import java.net.URL
import scala.concurrent.Future

class UploadedBusinessRatesBillControllerSpec extends ControllerSpecSupport with ScalaFutures {
  val view: UploadedBusinessRateBillView = inject[UploadedBusinessRateBillView]
  def controller: UploadedBusinessRatesBillController = new UploadedBusinessRatesBillController(
    mockUploadProgressTracker,
    view,
    mockAuthJourney,
    mockMandatoryCheck,
    mockPropertyLinkingRepo,
    mcc
  )
  val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty, evidenceDocumentUploadId = Some("12345"))
  val fileName: String = "filename.png"
  val fileExtension: String = ".png"
  val fileUrl: URL = url"http://example.com/dummyLink"
  val fileSize: Option[Long] = Some(120L)
  val uploadId = UploadId.generate()
  val checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100"

  "UploadedBusinessRatesBillController show()" must {
    "Return OK and the correct view" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(Some(UploadedSuccessfully(fileName, fileExtension, fileUrl, fileSize, checksum))))
      when(mockPropertyLinkingRepo.insertEvidenceDocument(any(), any(), any(), any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      val result = controller.show(uploadId)(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include("")
    }
    
    "Exception when no property returned" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))
      recoverToExceptionIf[NotFoundException] {
        controller.show(uploadId)(authenticatedFakeRequest)
      }.map { ex =>
        ex.getMessage mustBe "Not found property on account"
      }
    }

    "Return Bad Request if there is no uploadId associated in the user session repository repo" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(None))
      val result = controller.show(uploadId)(authenticatedFakeRequest)
      status(result) mustBe BAD_REQUEST
    }

    "Exception when no credId in request" in {
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockUploadProgressTracker.getUploadResult(any())).thenReturn(Future.successful(Some(UploadedSuccessfully(fileName, fileExtension, fileUrl, fileSize, checksum))))
      when(mockPropertyLinkingRepo.insertEvidenceDocument(any(), any(), any(), any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      recoverToExceptionIf[NotFoundException] {
        controller.show(uploadId)(authenticatedFakeRequest)
      }.map { ex =>
        ex.getMessage mustBe "No credId found in request"
      }
    }
  }

  "buildInProgressOrFailedSummaryList() must build the correct summary list" must {
    "the Upload Status is set to inProgress" in {
      val expectedSummaryList =
        SummaryList(List(
          SummaryListRow(
            key = Key(Text("Uploading")),
            value = Value(HtmlContent("""<span id="uploading-id"></span>"""))
          )
        ))
      val actualSummaryList = controller.buildInProgressOrFailedSummaryList("Uploading")

      actualSummaryList mustBe expectedSummaryList
    }

    "the Upload Status is set to Failed" in {
      val expectedSummaryList =
        SummaryList(List(
          SummaryListRow(
            key = Key(Text("Failed")),
            value = Value(HtmlContent("""<span id="failed-id"></span>"""))
          )
        ))
      val actualSummaryList = controller.buildInProgressOrFailedSummaryList("Failed")

      actualSummaryList mustBe expectedSummaryList
    }
  }

    "buildSuccessSummaryList()" must {
      "build the correct SummaryList when the Upload Status is to Successful" in {
        val expectedSummaryList: SummaryList = SummaryList(
          rows = Seq(
            NGRSummaryListRow(
              titleMessageKey = fileName,
              captionKey = None,
              value = Seq(messages("uploadedBusinessRatesBill.uploaded")),
              changeLink = Some(Link(Call("GET", routes.RemoveBusinessRatesBillController.show.url), "remove-link", "Remove")),
              titleLink = Some(Link(Call("GET", fileUrl.toString), "file-download-link", "")),
              valueClasses = Some("govuk-tag govuk-tag--green")
            )
          ).map(summarise),
          classes = "govuk-summary-list--long-key"
        )

        val actualSummaryList = controller.buildSuccessSummaryList(fileName, fileUrl.toString)

        actualSummaryList mustBe expectedSummaryList
      }
      
    }
}
