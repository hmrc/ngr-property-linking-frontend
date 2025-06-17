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
import org.mongodb.scala.SingleObservableFuture
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Indexes.ascending
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.FrontendAppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UpscanResponse

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
case class UpscanRepo @Inject()(mongo: MongoComponent,
                                config: FrontendAppConfig
                                )(implicit ec: ExecutionContext)
  extends PlayMongoRepository[UpscanResponse](
    collectionName = "upscanResponse",
    mongoComponent = mongo,
    domainFormat = UpscanResponse.format,
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

  def upsertUpscanResponse(upscanResponse: UpscanResponse): Future[Boolean] = {
    val errorMsg = s"upscanResponse has not been inserted"

    collection.replaceOne(
      filter = equal("credId.value", upscanResponse.credId.value),
      replacement = upscanResponse,
      options = ReplaceOptions().upsert(true)
    ).toFuture().transformWith {
      case Success(result) =>
        logger.info(s"upscanResponse has been upserted for credId: ${upscanResponse.credId.value}")
        result.wasAcknowledged()
        Future.successful(true)
      case Failure(exception) =>
        logger.error(s"credId: ${upscanResponse.credId.value} " + errorMsg)
        Future.failed(new IllegalStateException(s"$errorMsg: ${exception.getMessage} ${exception.getCause}"))
    }
  }

  def findByCredId(credId: CredId): Future[Option[UpscanResponse]] = {
    collection.find(
      equal("credId.value", credId.value)
    ).headOption()
  }
}

