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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.*

import scala.concurrent.Future

class UpscanConnectorSpec extends MockHttpV2 {
  private val upscanConnector: UpscanConnector = new UpscanConnector(mockHttpClientV2, mockConfig)
  private val formFields: Map[String, String] = Map("key" -> "value")
  private val upScanFileReference = UpscanFileReference("ref")
  private val postTarget = "postTarget"

  "Calling initiate()" should {
    "Successfully return a successful upload" in {
      val preparedUpload = PreparedUpload(
        reference = Reference("ref"),
        uploadRequest = UploadForm(
          href = postTarget,
          fields = formFields
        )
      )

      setupMockHttpV2PostWithHeaderCarrier(
        s"${mockConfig.upscanHost}/upscan/v2/initiate",
        Seq("Content-Type" -> "application/json")
      )(preparedUpload)

      val result: Future[UpscanInitiateResponse] = upscanConnector.initiate(Some("Success"), Some("Failure"))

      whenReady(result) { res =>
        res.fileReference mustBe upScanFileReference
        res.postTarget mustBe postTarget
        res.formFields mustBe formFields
      }
    }
  }
}
