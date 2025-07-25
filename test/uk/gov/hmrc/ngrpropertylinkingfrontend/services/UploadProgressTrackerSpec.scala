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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import org.mongodb.scala.bson.BsonDocument
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{Reference, UploadDetails, UploadId, UploadStatus}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FileUploadRepo
import org.mongodb.scala.bson.collection.immutable.Document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UploadProgressTrackerSpec extends TestSupport with TestData with DefaultPlayMongoRepositorySupport[UploadDetails]{

  override val repository: FileUploadRepo = FileUploadRepo(mongoComponent, mockConfig)
  val progressTracker = UploadProgressTracker(repository)

  override def beforeEach(): Unit = {
    super.beforeEach()
    repository.deleteAll().futureValue
  }

  "UploadProgressTracker" should  {
    "continue workflow" in {

      val reference = Reference("12345")
      val id = UploadId("12345")
      val downloadUrl = url"https://www.some-site.com/a-file.txt"
      val expectedStatus = UploadStatus.UploadedSuccessfully("name", "mimeType", downloadUrl, size = Some(123))

      progressTracker.requestUpload(id, reference).futureValue
      progressTracker.registerUploadResult(reference, expectedStatus).futureValue
      val result = progressTracker.getUploadResult(id).futureValue

      result mustBe Some(expectedStatus)
      
    }
    
  }

}
