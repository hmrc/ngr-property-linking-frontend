/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import play.api.http.Status.*
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{atLeastOnce, verify, when}
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{Reference, UploadDetails, UploadId, UploadStatus}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{FileUploadRepo, PropertyLinkingRepo}
import uk.gov.hmrc.objectstore.client.*
import uk.gov.hmrc.objectstore.client.play.PlayObjectStoreClient



import java.net.URL
import java.time.Instant
import scala.concurrent.Future


class UploadProgressTrackerSpec
  extends TestSupport
    with TestData
    with DefaultPlayMongoRepositorySupport[UploadDetails] {

  override val repository: FileUploadRepo = FileUploadRepo(mongoComponent, mockConfig)
  val objectStoreClient                   = mock[PlayObjectStoreClient]
  val mockPropertyLinkingRepo: PropertyLinkingRepo = mock[PropertyLinkingRepo]
  lazy val mockHttpClientV2: HttpClientV2         = Mockito.mock(classOf[HttpClientV2])
  lazy val mockNgrLogger: NGRLogger               = mock[NGRLogger]
  val progressTracker =
    UploadProgressTracker(repository, mockConfig, objectStoreClient, mockPropertyLinkingRepo, mockHttpClientV2, mockNgrLogger)

  override def beforeEach(): Unit = {
    super.beforeEach()
    repository.deleteAll().futureValue
  }

  given HeaderCarrier = mock[HeaderCarrier]

  // ------------------------------------------------------------
  // Existing test you had
  // ------------------------------------------------------------
  "UploadProgressTracker" should:
    "coordinate workflow" in :
      val reference      = Reference("reference")
      val id             = UploadId("upload-id")
      val downloadUrl    = url"https://www.some-site.com/a-file.txt"
      val expectedStatus = UploadStatus.UploadedSuccessfully(
        name       = "name",
        mimeType   = "mimeType",
        downloadUrl = downloadUrl,
        size       = Some(123),
        checksum   = "a142ed16d596494528e264ffdd5bfbd1188243e0ed1afc8768bcd5d76eb9c4f1"
      )

      when(
        objectStoreClient.uploadFromUrl(
          from            = any[URL],
          to              = any[Path.File],
          retentionPeriod = any[RetentionPeriod],
          contentType     = any[Option[String]],
          contentMd5      = any[Option[Md5Hash]],
          contentSha256   = any[Option[Sha256Checksum]],
          owner           = any[String]
        )(using any[HeaderCarrier])
      ).thenReturn(
        Future.successful(
          ObjectSummaryWithMd5(
            location      = Path.File("/some/file.txt"),
            contentLength = 100,
            contentMd5    = Md5Hash("md5hash"),
            lastModified  = Instant.now()
          )
        )
      )

      progressTracker.requestUpload(id, reference).futureValue
      progressTracker.registerUploadResult(reference, expectedStatus).futureValue

      val result = progressTracker.getUploadResult(id).futureValue
      result mustBe Some(expectedStatus)

}
