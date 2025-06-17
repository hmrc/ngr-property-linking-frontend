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

import com.google.inject.Singleton
import com.mongodb.client.model.Indexes.descending
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import org.mongodb.scala.model.Updates.combine
import org.mongodb.scala.model.*
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.FrontendAppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import org.mongodb.scala.SingleObservableFuture

import java.time.Instant
import scala.util.{Failure, Success}
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
case class PropertyLinkingRepo @Inject()(mongo: MongoComponent,
                                               config: FrontendAppConfig
                                              )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[PropertyLinkingUserAnswers](
    collectionName = "propertyLinking",
    mongoComponent = mongo,
    domainFormat = PropertyLinkingUserAnswers.format,
    indexes = Seq(
      IndexModel(
        descending("createdAt"),
        IndexOptions()
          .unique(false)
          .name("createdAt")
          .expireAfter(config.timeToLive.toLong, TimeUnit.HOURS)
      ),
      IndexModel(
        ascending("credId.value"),
        IndexOptions()
          .background(false)
          .name("credId.value")
          .unique(true)
          .partialFilterExpression(Filters.gte("credId.value", ""))
      )
    )
  ) with Logging {

  override lazy val requiresTtlIndex: Boolean = false

  private def filterByCredId(credId: CredId): Bson = equal("credId.value", credId.value)

  def upsertProperty(propertyLinking: PropertyLinkingUserAnswers): Future[Boolean] = {
    val errorMsg = s"Property has not been inserted"

    collection.replaceOne(
      filter = equal("credId.value", propertyLinking.credId.value),
      replacement = propertyLinking,
      options = ReplaceOptions().upsert(true)
    ).toFuture().transformWith {
      case Success(result) =>
        logger.info(s"PropertyLinking has been upsert for credId: ${propertyLinking.credId.value}")
        Future.successful(result.wasAcknowledged())
      case Failure(exception) =>
        logger.error(errorMsg)
        Future.failed(new IllegalStateException(s"$errorMsg: ${exception.getMessage} ${exception.getCause}"))
    }
  }

  def findAndUpdateByCredId(credId: CredId, updates: Bson*): Future[Option[PropertyLinkingUserAnswers]] = {
    collection.findOneAndUpdate(filterByCredId(credId),
        combine(updates :+ Updates.set("createdAt", Instant.now()): _*),
        FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))
      .toFutureOption()
  }

  def insertCurrentRatepayer(credId: CredId ,currentRatepayer: String): Future[Option[PropertyLinkingUserAnswers]] = {
    findAndUpdateByCredId(credId, Updates.set("currentRatepayer", currentRatepayer))
  }

  def insertBusinessRatesBill(credId: CredId, businessRatesBill: String): Future[Option[PropertyLinkingUserAnswers]] = {
    findAndUpdateByCredId(credId, Updates.set("businessRatesBill", businessRatesBill))
  }
  
  def findByCredId(credId: CredId): Future[Option[PropertyLinkingUserAnswers]] = {
    collection.find(
      equal("credId.value", credId.value)
    ).headOption()
  }
}
