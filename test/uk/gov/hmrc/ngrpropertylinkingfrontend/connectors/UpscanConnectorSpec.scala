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

package uk.gov.hmrc.ngrpropertylinkingfrontend.connectors

import uk.gov.hmrc.ngrpropertylinkingfrontend.mocks.MockHttpV2
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanInitiateResponse, UpscanReference, UploadForm}

import scala.concurrent.Future

class UpscanConnectorSpec extends MockHttpV2 {
  private val upscanConnector: UpscanConnector = new UpscanConnector(mockHttpClientV2, mockConfig)
  private val reference: String = "testReference"
  private val formFields: Map[String, String] = Map("key" -> "value")

  "initiate()" when {
    "Successfully return a PreparedUpload" in {
      val response: UpscanInitiateResponse = UpscanInitiateResponse(UpscanReference(reference), UploadForm("href", formFields))
      setupMockHttpV2PostWithHeaderCarrier(s"${mockConfig.upscanHost}/upscan/v2/initiate", Seq("Content-Type" -> "application/json"))(response)
      val result: Future[UpscanInitiateResponse] = upscanConnector.initiate()
      result.futureValue.reference mustBe UpscanReference(reference)
      result.futureValue.uploadRequest mustBe UploadForm("href", formFields)
    }
  }
}
