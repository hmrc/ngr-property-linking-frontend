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

import play.api.test.Helpers.await
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{TestData, TestSupport}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.LookUpVMVProperties
import play.api.test.Helpers.defaultAwaitTimeout
import org.mongodb.scala.SingleObservableFuture
import org.scalatest.matchers.should.Matchers.shouldBe
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId

class FindAPropertyRepoSpec extends TestSupport with TestData

  with DefaultPlayMongoRepositorySupport[LookUpVMVProperties] {
  override val repository: FindAPropertyRepo = app.injector.instanceOf[FindAPropertyRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  "repository" can {
    "save a new LookUpVMVProperties" when {
      "correct LookUpVMVProperties has been supplied" in {
        val isSuccessful = await(repository.upsertProperty(LookUpVMVProperties(credId, properties1)))
        isSuccessful shouldBe true
        val actual = await(repository.findByCredId(credId))
        actual shouldBe Some(LookUpVMVProperties(credId, properties1))
      }
      "missing credId" in {
        val missingCredId = LookUpVMVProperties(credId = CredId(null), properties1)
        val exception = intercept[IllegalStateException] {
          await(repository.upsertProperty(missingCredId))
        }
        exception.getMessage contains "Property has not been inserted" shouldBe true
      }
    }

    "find LookUpVMVProperties by cred id" when {
      "correct LookUpVMVProperties has been returned" in {
        await(repository.upsertProperty(LookUpVMVProperties(credId, properties1)))
        val isSuccessful = await(repository.findByCredId(credId))

        isSuccessful mustBe defined
        val response = isSuccessful.get
        val expected = LookUpVMVProperties(credId, properties1)
        response shouldBe expected
      }

      "credId doesn't exist in mongoDB" in {
        val actual = await(repository.findByCredId(credId))
        actual mustBe None
      }
    }
  }
}
