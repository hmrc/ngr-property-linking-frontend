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
import org.mockito.Mockito.*
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId

import java.net.URL
import java.time.Instant
import scala.concurrent.Future

class UpscanCallbackControllerSpec extends ControllerSpecSupport {

  def controller() = new UpscanCallbackController(
    mockUpscanRepo,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mcc
  )(mockConfig, ec)

  val credIdX: CredId = CredId("1234")
  val reference: UpscanReference = UpscanReference("123456789")

  val existingRecord: UpscanRecord = UpscanRecord(
    credId = credIdX,
    reference = reference,
    status = "INITIATED",
    downloadUrl = None,
    fileName = None,
    failureReason = None,
    failureMessage = None
  )

  "handleUpscanCallback()" should {

    "return OK and update repo when callback is SUCCESS" in {
      val callbackJson: JsValue = Json.obj(
        "fileStatus" -> "READY",
        "reference" -> reference.value,
        "downloadUrl" -> "https://example.com/download-file",
        "uploadDetails" -> Json.obj(
          "uploadTimestamp" -> Instant.now.toString,
          "checksum" -> "abc123",
          "fileMimeType" -> "application/pdf",
          "fileName" -> "test.pdf",
          "size" -> 123456
        )
      )

      when(mockUpscanRepo.findByReference(reference)).thenReturn(Future.successful(Some(existingRecord)))
      when(mockUpscanRepo.upsertUpscanRecord(any[UpscanRecord])).thenReturn(Future.successful(true))


      val result = controller().handleUpscanCallback()(fakeRequest.withBody(callbackJson))

      status(result) mustBe OK
    }

    "return OK and update repo when callback is FAILURE" in {
      val callbackJson: JsValue = Json.obj(
        "fileStatus" -> "FAILED",
        "reference" -> reference.value,
        "failureDetails" -> Json.obj(
          "failureReason" -> "QUARANTINE",
          "message" -> "File contains a virus"
        )
      )

      when(mockUpscanRepo.findByReference(reference)).thenReturn(Future.successful(Some(existingRecord)))
      when(mockUpscanRepo.upsertUpscanRecord(any[UpscanRecord])).thenReturn(Future.successful(true))


      val result = controller().handleUpscanCallback()(fakeRequest.withBody(callbackJson))

      status(result) mustBe OK
    }

    "return 500 if no existing record is found for reference" in {
      val callbackJson: JsValue = Json.obj(
        "fileStatus" -> "READY",
        "reference" -> reference.value,
        "downloadUrl" -> "https://example.com/download-file",
        "uploadDetails" -> Json.obj(
          "uploadTimestamp" -> Instant.now.toString,
          "checksum" -> "abc123",
          "fileMimeType" -> "application/pdf",
          "fileName" -> "test.pdf",
          "size" -> 123456
        )
      )

      when(mockUpscanRepo.findByReference(reference)).thenReturn(Future.successful(None))

      val result = controller().handleUpscanCallback()(fakeRequest.withBody(callbackJson)).failed.futureValue

      result mustBe a[RuntimeException]
      result.getMessage must include("Upscan record not found")
    }


    "return 400 for malformed or missing JSON discriminator" in {
      val badJson: JsValue = Json.obj(
        "reference" -> reference.value,
        "uploadDetails" -> Json.obj(
          "uploadTimestamp" -> Instant.now.toString,
          "checksum" -> "12345",
          "fileMimeType" -> "application/pdf",
          "fileName" -> "test.pdf",
          "size" -> 123456
        )
      )

      val result = controller().handleUpscanCallback()(fakeRequest.withBody(badJson))

      status(result) mustBe BAD_REQUEST
    }

    "return 400 for unrecognized fileStatus value" in {
      val callbackJson: JsValue = Json.obj(
        "fileStatus" -> "UNRECOGNISED_FILE_STATUS",
        "reference" -> reference.value
      )

      val result = controller().handleUpscanCallback()(fakeRequest.withBody(callbackJson))

      status(result) mustBe BAD_REQUEST
    }
  }
}