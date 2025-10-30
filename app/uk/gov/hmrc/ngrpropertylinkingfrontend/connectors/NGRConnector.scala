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

import play.api.http.Status.CREATED
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.{VMVProperties, VMVProperty, Valuation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.{CredId, RatepayerRegistrationValuation}

import java.net.URL
import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NGRConnector @Inject()(http: HttpClientV2,
                             appConfig: AppConfig,
                             logger: NGRLogger)
                            (implicit ec: ExecutionContext){

  private def url(path: String): URL = url"${appConfig.nextGenerationRatesHost}/next-generation-rates/$path"

  def getRatepayer(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[RatepayerRegistrationValuation]] = {
    implicit val rds: HttpReads[RatepayerRegistrationValuation] = readFromJson
    val model: RatepayerRegistrationValuation = RatepayerRegistrationValuation(credId, None)
    http.get(url("get-ratepayer"))
      .withBody(Json.toJson(model))
      .execute[Option[RatepayerRegistrationValuation]]
  }

  def getPropertyLinkingUserAnswers(credId: CredId)(implicit hc: HeaderCarrier): Future[Option[PropertyLinkingUserAnswers]] = {
    implicit val rds: HttpReads[PropertyLinkingUserAnswers] = readFromJson
    //Because vmvProperty in PropertyLinkingUserAnswers isn't an option so a dummy VMVProperty has been passed in
    //which will not affect mongoDB data since it's only use credId to find PropertyLinkingUserAnswers
    val model: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId, dummyVmvProperty)
    http.get(url("get-property-linking-user-answers"))
      .withBody(Json.toJson(model))
      .execute[Option[PropertyLinkingUserAnswers]]
  }

  def upsertPropertyLinkingUserAnswers(model: PropertyLinkingUserAnswers)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.post(url("upsert-property-linking-user-answers"))
      .withBody(Json.toJson(model))
      .execute[HttpResponse]
      .map { response =>
        logger.info("Upsert Property Linking UserAnswers" + model)
        response.status match {
          case CREATED => response
          case _ => throw new Exception(s"${response.status}: ${response.body}")
        }
    }
  }

  private val dummyVmvProperty: VMVProperty = VMVProperty(
    uarn = 11905603000L,
    localAuthorityReference = "2191322564521",
    addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
    localAuthorityCode = "4720",
    valuations = List(
      Valuation(
        assessmentRef = 25141561000L,
        assessmentStatus = "CURRENT",
        rateableValue = Some(9300),
        scatCode = Some("249"),
        descriptionText = "SHOP AND PREMISES",
        effectiveDate = LocalDate.of(2023, 4, 1),
        currentFromDate = LocalDate.of(2023, 4, 1),
        currentToDate = Some(LocalDate.of(2023, 4, 1)),
        listYear = "2023",
        primaryDescription = "CS",
        allowedActions = List(
          "check",
          "challenge",
          "viewDetailedValuation",
          "propertyLink",
          "similarProperties"
        ),
        listType = "current",
      )
    )
  )
}
