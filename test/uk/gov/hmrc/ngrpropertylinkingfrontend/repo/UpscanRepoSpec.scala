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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanRecord}

class UpscanRepoSpec  extends TestSupport with TestData
  with DefaultPlayMongoRepositorySupport[UpscanRecord] {
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


  "repository" can {
    "save a new UpscanResponse" when {
      "correct UpscanResponse has been supplied" in {
        val isSuccessful = await(repository.upsertUpscanResponse(upscanResponse))
        isSuccessful shouldBe true
        val actual = await(repository.findByCredId(credId))
        actual shouldBe Some(upscanResponse)
      }

    }

    "find UpscanResponse by cred id" when {
      "correct UpscanResponse has been returned" in {
        await(repository.upsertUpscanResponse(upscanRecord))
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
