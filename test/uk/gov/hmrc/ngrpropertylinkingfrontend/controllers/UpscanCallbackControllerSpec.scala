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

import org.scalatest.matchers.should.Matchers
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanCallbackFailure, UpscanCallbackSuccess, UpscanRecord, FileStatus, FailureDetails}
import scala.concurrent.Future

class UpscanCallbackControllerSpec extends ControllerSpecSupport {
  val controller: UpscanCallbackController = new UpscanCallbackController(
    mockUpscanRepo,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(mockConfig, ec)

  val testCredId = CredId("1234")
  val testReference = "ref123"
  val testDownloadUrl = "http://example.com/download"
  val testFileName = "testfile.pdf"

  "UpscanCallbackController" must {
    "handleUpscanCallback" must {
      "return OK when a successful callback updates an existing UpscanRecord" in {
        val callback = UpscanCallbackSuccess(
          reference = testReference,
          downloadUrl = testDownloadUrl,
          uploadDetails = UploadDetails(fileName = testFileName, fileMimeType = "application/pdf", uploadTimestamp = "2025-06-18T16:24:00Z", checksum = "abc123")
        )
        val existingRecord = UpscanRecord(
          credId = testCredId,
          reference = testReference,
          status = "PENDING",
          downloadUrl = None,
          fileName = None,
          failureReason = None,
          failureMessage = None
        )
        when(mockUpscanRepo.findByReference(testReference)).thenReturn(Future.successful(Some(existingRecord)))
        when(mockUpscanRepo.upsertUpscanRecord(any[UpscanRecord])).thenReturn(Future.successful(()))

        val result = controller.handleUpscanCallback()(authenticatedFakeRequest.withBody(Json.toJson(callback)))

        status(result) mustBe OK
      }

      "return OK when a failed callback updates an existing UpscanRecord" in {
        val callback = UpscanCallbackFailure(
          reference = testReference,
          failureDetails = FailureDetails(failureReason = "QUARANTINE", message = "File contains a virus")
        )
        val existingRecord = UpscanRecord(
          credId = testCredId,
          reference = testReference,
          status = "PENDING",
          downloadUrl = None,
          fileName = None,
          failureReason = None,
          failureMessage = None
        )
        when(mockUpscanRepo.findByReference(testReference)).thenReturn(Future.successful(Some(existingRecord)))
        when(mockUpscanRepo.upsertUpscanRecord(any[UpscanRecord])).thenReturn(Future.successful(()))

        val result = controller.handleUpscanCallback()(authenticatedFakeRequest.withBody(Json.toJson(callback)))

        status(result) mustBe OK
      }

      "return INTERNAL_SERVER_ERROR when no UpscanRecord is found for the reference" in {
        val callback = UpscanCallbackSuccess(
          reference = testReference,
          downloadUrl = testDownloadUrl,
          uploadDetails = UploadDetails(fileName = testFileName, fileMimeType = "application/pdf", uploadTimestamp = "2025-06-18T16:24:00Z", checksum = "abc123")
        )
        when(mockUpscanRepo.findByReference(testReference)).thenReturn(Future.successful(None))

        val result = controller.handleUpscanCallback()(authenticatedFakeRequest.withBody(Json.toJson(callback)))

        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) must include("Upscan record not found for reference: " + testReference)
      }

      "return INTERNAL_SERVER_ERROR when the JSON body is invalid" in {
        val invalidJson = Json.parse("""{"invalid": "data"}""")
        val result = controller.handleUpscanCallback()(authenticatedFakeRequest.withBody(invalidJson))

        status(result) mustBe INTERNAL_SERVER_ERROR
        contentAsString(result) must include("Invalid JSON")
      }
    }
  }
}