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

import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.RemoveBusinessRatesBillView
import org.scalatest.matchers.should.Matchers.shouldBe
import java.net.URL
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{await, contentAsString, defaultAwaitTimeout, redirectLocation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow, PropertyLinkingUserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import scala.concurrent.Future

class RemoveBusinessRatesBillControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  val view: RemoveBusinessRatesBillView = inject[RemoveBusinessRatesBillView]
  val fileName: String = "testFileName"
  val evidenceDocumentUploadId: String = "12345"
  val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(
    credId = credId,
    vmvProperty = testVmvProperty,
    evidenceDocumentName = Some(fileName),
    evidenceDocumentUrl = Some("testUrl.com"),
    evidenceDocumentUploadId = Some(evidenceDocumentUploadId))
  val incompletePropertyLinkingUserAnswers: PropertyLinkingUserAnswers = propertyLinkingUserAnswers.copy(
    evidenceDocumentName = None,
    evidenceDocumentUrl = None,
    evidenceDocumentUploadId = None)

  def controller() = new RemoveBusinessRatesBillController(
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mcc
  )

  "RemoveBusinessRatesBillController" must {
    "show() returns OK and the correct view" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))

      val result = controller().show()(authenticatedFakeRequest)
      val content = contentAsString(result)
      content must include("Are you sure you want to remove this file?")
    }

    "show() throws an exception when no CredId in request" in {
      mockRequest()

      val exception = intercept[NotFoundException] {
        await(controller().show()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "CredId not found in RemoveBusinessRatesBillController.show()"
    }

    "show() throws an exception when fields are missing" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(incompletePropertyLinkingUserAnswers)))

      val exception = intercept[NotFoundException] {
        await(controller().show()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "Fields not found in RemoveBusinessRatesBillController.show()"
    }

    "show() throws an exception when no property is returned" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))

      val exception = intercept[NotFoundException] {
        await(controller().show()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "Property not found in RemoveBusinessRatesBillController.show()"
    }

    "remove() returns OK and the correct view" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockPropertyLinkingRepo.deleteEvidenceDocument(any())).thenReturn(Future.successful(true))

      val result = controller().remove()(authenticatedFakeRequest)
      redirectLocation(result) shouldBe Some(routes.UploadedBusinessRatesBillController.show(UploadId(evidenceDocumentUploadId), Some(fileName)).url)
    }

    "remove() throws an exception when no CredId in request" in {
      mockRequest()

      val exception = intercept[NotFoundException] {
        await(controller().remove()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "CredId not found in RemoveBusinessRatesBillController.remove()"
    }

    "remove() throws an exception when no EvidenceDocumentUploadId is found" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(incompletePropertyLinkingUserAnswers)))

      val exception = intercept[NotFoundException] {
        await(controller().remove()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "EvidenceDocumentUploadId not found in RemoveBusinessRatesBillController.remove()"
    }

    "remove() throws an exception when no property is returned" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(None))

      val exception = intercept[NotFoundException] {
        await(controller().remove()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "Property not found in RemoveBusinessRatesBillController.remove()"
    }

    "remove() throws an exception when the deletion fails" in {
      mockRequest(hasCredId = true)
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      when(mockPropertyLinkingRepo.deleteEvidenceDocument(any())).thenReturn(Future.successful(false))

      val exception = intercept[RuntimeException] {
        await(controller().remove()(authenticatedFakeRequest))
      }
      exception.getMessage mustBe "Failed to delete evidence document in RemoveBusinessRatesBillController.remove()"
    }

    "makeSummaryList() builds the correct SummaryList" in {
      val downloadUrl: String = "http://localhost:1000/testDownloadUrl.com"
      val expectedSummaryList: SummaryList = SummaryList(
        Seq(
          NGRSummaryListRow(
            titleMessageKey = fileName,
            captionKey = None,
            value = Seq(messages("uploadedBusinessRatesBill.uploaded")),
            changeLink = None,
            titleLink = Some(Link(Call("GET", downloadUrl), "file-download-link", "")),
            valueClasses = Some("govuk-tag govuk-tag--green")
          )
        ).map(summarise))
      val actualSummaryList: SummaryList = controller().buildSummaryList(fileName, URL(downloadUrl))

      actualSummaryList mustBe expectedSummaryList
    }
  }
}
