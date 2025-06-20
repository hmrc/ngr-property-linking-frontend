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
import play.api.http.Status.OK
import play.api.i18n.Messages
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{PropertyLinkingUserAnswers, UpscanInitiateResponse, UpscanReference}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm

import scala.concurrent.Future

class UploadBusinessRatesBillControllerSpec extends ControllerSpecSupport {
  val pageTitle = "Upload your business rates bill"
  val view: UploadBusinessRatesBillView = inject[UploadBusinessRatesBillView]
  val uploadFormData: uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm = inject[uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm]
  val uploadForm: uk.gov.hmrc.ngrpropertylinkingfrontend.models.UploadForm = uk.gov.hmrc.ngrpropertylinkingfrontend.models.UploadForm("", Map("key" -> "value"))

  val controller: UploadBusinessRatesBillController = new UploadBusinessRatesBillController(view, mockUpscanConnector, mockUpscanRepo, uploadFormData, mockAuthJourney, mockIsRegisteredCheck, mockPropertyLinkingRepo, mcc)(mockConfig)

  val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = testVmvProperty)
  
  "Upload business rates bill controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        mockRequest(hasCredId = true)
        when(mockUpscanConnector.initiate(any())).thenReturn(Future.successful(UpscanInitiateResponse(UpscanReference("ref"), uploadForm)))
        when(mockUpscanRepo.upsertUpscanRecord(any())).thenReturn(Future.successful(true))
        when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
        val result = controller.show(None)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }


    def testErrorCase(errorCode: String, expectedMessage: String): Unit = {
      mockRequest(hasCredId = true)
      when(mockUpscanConnector.initiate(any())).thenReturn(Future.successful(UpscanInitiateResponse(UpscanReference("ref"), uploadForm)))
      when(mockUpscanRepo.upsertUpscanRecord(any())).thenReturn(Future.successful(true))
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      val result = controller.show(Some(errorCode))(authenticatedFakeRequest)
      status(result) mustBe OK
      val content = contentAsString(result)
      content must include(Messages(expectedMessage))
    }


    "display 'no file selected' error for InvalidArgument" in {
      testErrorCase("InvalidArgument", "uploadBusinessRatesBill.error.noFileSelected")
    }

    "display 'file too large' error for EntityTooLarge" in {
      testErrorCase("EntityTooLarge", "uploadBusinessRatesBill.error.exceedsMaximumSize")
    }

    "display 'file too small' error for EntityTooSmall" in {
      testErrorCase("EntityTooSmall", "uploadBusinessRatesBill.error.fileTooSmall")
    }

    "display 'virus detected' error for QUARANTINE" in {
      testErrorCase("QUARANTINE", "uploadBusinessRatesBill.error.virusDetected")
    }

    "display 'problem with upload' error for REJECTED" in {
      testErrorCase("REJECTED", "uploadBusinessRatesBill.error.problemWithUpload")
    }

    "display 'problem with upload' error for UNKNOWN reason" in {
      testErrorCase("UNKNOWN123", "uploadBusinessRatesBill.error.problemWithUpload")
    }


  }





}
