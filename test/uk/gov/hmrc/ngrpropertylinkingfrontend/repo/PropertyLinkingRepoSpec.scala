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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{CurrentRatepayer, PropertyLinkingUserAnswers}

class PropertyLinkingRepoSpec extends TestSupport with TestData

  with DefaultPlayMongoRepositorySupport[PropertyLinkingUserAnswers] {
  override val repository: PropertyLinkingRepo = app.injector.instanceOf[PropertyLinkingRepo]

  override def beforeEach(): Unit = {
    await(repository.collection.drop().toFuture())
    await(repository.ensureIndexes())
  }

  "repository" can {
    "save a new PropertyLinkingUserAnswer" when {
      "correct LookUpAddresses has been supplied" in {
        val isSuccessful = await(
          repository.upsertProperty(PropertyLinkingUserAnswers(
            credId,
            testVmvProperty
            )))
        isSuccessful shouldBe true
        val actual = await(repository.findByCredId(credId))
        actual shouldBe Some(PropertyLinkingUserAnswers(credId, testVmvProperty))
      }
      "missing credId" in {
        val missingCredId = PropertyLinkingUserAnswers(credId = CredId(null), testVmvProperty)
        val exception = intercept[IllegalStateException] {
          await(repository.upsertProperty(missingCredId))
        }
        exception.getMessage contains "Property has not been inserted" shouldBe true
      }
    }

    "insertCurrentRatepayer by cred id" when {
      "correct PropertyLinkingUserAnswer has been returned" in {
        val isSuccessful = await(repository.upsertProperty(PropertyLinkingUserAnswers(
          credId,
          testVmvProperty
        )))
        isSuccessful shouldBe true
        await(repository.insertCurrentRatepayer(credId = credId, currentRatepayer = true, maybeRatepayerDate = None))
        val actual: PropertyLinkingUserAnswers = await(repository.findByCredId(credId)).get
        val expected = PropertyLinkingUserAnswers(credId, testVmvProperty, Some(CurrentRatepayer(true, None)))
        actual shouldBe expected
      }
    }

    "insertConnectionToProperty by cred id" when {
      "correct PropertyLinkingUserAnswer has been returned" in {
        val isSuccessful = await(repository.upsertProperty(PropertyLinkingUserAnswers(
          credId,
          testVmvProperty
        )))
        isSuccessful shouldBe true
        await(repository.insertConnectionToProperty(credId = credId, connectionToProperty = "Owner"))
        val actual: PropertyLinkingUserAnswers = await(repository.findByCredId(credId)).get
        val expected = PropertyLinkingUserAnswers(credId, testVmvProperty, None, None, Some("Owner"))
        actual shouldBe expected
      }
    }
    
    "find PropertyLinkingUserAnswer by cred id" when {
      "correct PropertyLinkingUserAnswer has been returned" in {
        await(repository.upsertProperty(PropertyLinkingUserAnswers(
          credId,
          testVmvProperty
        )))
        val isSuccessful = await(repository.findByCredId(credId))

        isSuccessful mustBe defined
        val response = isSuccessful.get
        val expected = PropertyLinkingUserAnswers(credId, testVmvProperty)
        response shouldBe expected
      }

      "credId doesn't exist in mongoDB" in {
        val actual = await(repository.findByCredId(credId))
        actual mustBe None
      }
    }
  }
} 
