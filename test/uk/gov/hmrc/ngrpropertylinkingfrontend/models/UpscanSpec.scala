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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanInitiateResponse.{format, uploadFormFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanReference.{referenceReader, referenceWrites}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanInitiateRequest.format
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanInitiateResponse.format

class UpscanSpec extends TestSupport {
  val testReference: UpscanReference = UpscanReference("testReference")
  val testReferenceJson: JsValue = Json.parse(""" "testReference" """)

  "Reference" should {
    "serialize to JSON" in {
      Json.toJson(testReference) mustBe testReferenceJson
    }
    "deserialize from JSON" in {
      testReferenceJson.as[UpscanReference] mustBe testReference
    }
  }
  
  val testUploadForm: UploadForm = UploadForm("href", Map("key" -> "value"))
  val testUploadFormJson: JsValue = Json.parse("""{"href":"href","fields":{"key":"value"}}""")

  "UploadForm" should {
    "serialize to JSON" in {
      Json.toJson(testUploadForm) mustBe testUploadFormJson
    }
    "deserialize from JSON" in {
      testUploadFormJson.as[UploadForm] mustBe testUploadForm
    }
  }
  
  val testPreparedUpload: UpscanInitiateResponse = UpscanInitiateResponse(testReference, testUploadForm)
  val testPreparedUploadJson: JsValue = Json.parse(
    """{"reference":"testReference","uploadRequest":{"href":"href","fields":{"key":"value"}}}""")

  "PreparedUpload" should {
    "serialize to JSON" in {
      Json.toJson(testPreparedUpload) mustBe testPreparedUploadJson
    }
    "deserialize from JSON" in {
      testPreparedUploadJson.as[UpscanInitiateResponse] mustBe testPreparedUpload
    }
  }
  
  val testUpscanInitiateRequest: UpscanInitiateRequest = UpscanInitiateRequest(
    callbackUrl = "http://callback.com",
    successRedirect = Some("http://success.com"),
    errorRedirect = Some("http://error.com"),
    minimumFileSize = Some(1000),
    maximumFileSize = Some(1000000))
  val testUpscanInitiateRequestJson: JsValue = Json.parse(
    """{"callbackUrl":"http://callback.com","successRedirect":"http://success.com","errorRedirect":"http://error.com","minimumFileSize":1000,"maximumFileSize":1000000}""")

  "UpscanInitiateRequest" should {
    "serialize to JSON" in {
      Json.toJson(testUpscanInitiateRequest) mustBe testUpscanInitiateRequestJson
    }
    "deserialize from JSON" in {
      testUpscanInitiateRequestJson.as[UpscanInitiateRequest] mustBe testUpscanInitiateRequest
    }
  }
  
  val testUpscanFileReference: UpscanFileReference = UpscanFileReference("fileRef")
  val testUpscanFileReferenceJson: JsValue = Json.parse("""{"reference":"fileRef"}""")

  "UpscanFileReference" should {
    "serialize to JSON" in {
      Json.toJson(testUpscanFileReference) mustBe testUpscanFileReferenceJson
    }
    "deserialize from JSON" in {
      testUpscanFileReferenceJson.as[UpscanFileReference] mustBe testUpscanFileReference
    }
  }
  
//  val testUpscanInitiateResponse: UpscanInitiateResponse = UpscanInitiateResponse(
//    fileReference = testUpscanFileReference,
//    postTarget = "postTarget",
//    formFields = Map("key" -> "value"))

  val testUpscanInitiateResponse: UpscanInitiateResponse = UpscanInitiateResponse(reference = UpscanReference("fileRef"), uploadRequest = UploadForm(href = "postTarget", fields = Map("key" -> "value")))


  val testUpscanInitiateResponseJson: JsValue = Json.parse(
    """{"reference":"fileRef","uploadRequest":{"href":"postTarget","fields":{"key":"value"}}}""")

  "UpscanInitiateResponse" should {
    "serialize to JSON" in {
      Json.toJson(testUpscanInitiateResponse) mustBe testUpscanInitiateResponseJson
    }
    "deserialize from JSON" in {
      testUpscanInitiateResponseJson.as[UpscanInitiateResponse] mustBe testUpscanInitiateResponse
    }
  }
}