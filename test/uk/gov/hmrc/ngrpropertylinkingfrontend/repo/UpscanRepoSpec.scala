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

package uk.gov.hmrc.ngrpropertylinkingfrontend.repo

import org.mongodb.scala.SingleObservableFuture
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanCallbackSuccess, UpscanCallbackUploadDetails}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{UploadDetails, UpscanResponse}

class UpscanRepoSpec  extends TestSupport with TestData
  with DefaultPlayMongoRepositorySupport[UpscanResponse] {
  override val repository: UpscanRepo = app.injector.instanceOf[UpscanRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  val upscanCallbackUploadDetails = UpscanCallbackUploadDetails(
                                        uploadTimeStamp = "2018-04-24T09:30:00Z",
                                        checksum = "placeholder",
                                        fileMimeType = "placeholder",
                                        fileName = "placeholder.pdf",
                                        size = 1000)

  val upscanCallbackSuccess = UpscanCallbackSuccess(
    reference = "placeHolder",
    downloadUrl = "placeHolder",
    uploadDetails = upscanCallbackUploadDetails
  )

//  val upscanResponse: UpscanResponse = UpscanResponse(
//    reference = "11370e18-6e24-453e-b45a-76d3e32ea33d",
//    downloadUrl =  "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
//    fileStatus =  "READY",
//    uploadDetails = UploadDetails(
//      fileName = "test.pdf",
//      fileMimeType =  "application/pdf",
//      uploadTimestamp =  "2018-04-24T09:30:00Z",
//      checksum = "396f101dd52e8b2ace0dcf5ed09b1d1f030e608938510ce46e7a5c7a4e775100",
//      size = 987
//    )
//  )

  "repository" can {
    "save a new UpscanResponse" when {
      "correct UpscanResponse has been supplied" in {
        val isSuccessful = await(repository.upsertUpscanResponse(upscanResponse))
        isSuccessful shouldBe true
        val actual = await(repository.findByCredId(credId))
        actual shouldBe Some(upscanResponse)
      }
//      "missing credId" in {
//        val missingCredId = upscanResponse.copy(credId = CredId(null))
//        val exception = intercept[IllegalStateException] {
//          await(repository.upsertUpscanResponse(missingCredId))
//        }
//        exception.getMessage contains "upscanResponse has not been inserted" shouldBe true
//      }
    }

    "find UpscanResponse by cred id" when {
      "correct UpscanResponse has been returned" in {
        await(repository.upsertUpscanResponse(upscanResponse))
        val isSuccessful = await(repository.findByCredId(credId))

        isSuccessful mustBe defined
        val response = isSuccessful.get
        val expected = upscanResponse
        response shouldBe expected
      }

      "credId doesn't exist in mongoDB" in {
        val actual = await(repository.findByCredId(credId))
        actual mustBe None
      }
    }
  }
}
