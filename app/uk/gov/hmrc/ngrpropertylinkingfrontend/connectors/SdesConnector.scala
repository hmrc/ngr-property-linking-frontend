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

import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpException, HttpReadsHttpResponse, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.SdesNotificationHttpParser.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SdesConnector @Inject() (
                                val config: AppConfig,
                                httpClient: HttpClientV2,
                                logger: NGRLogger
                              )(implicit ec: ExecutionContext)extends HttpReadsHttpResponse {
  def notifySdes(ftn: FileTransferNotification)(implicit hc: HeaderCarrier): Future[SdesNotificationResult]  =
    httpClient
      .post(url"${config.sdesNotificationUrl}")
      .withBody(Json.toJson(ftn)(FileTransferNotification.format))
      .setHeader(
        "Csrf-Token" -> "nocheck",
        "x-client-id" -> config.sdesAuthorizationToken,
        "Content-Type" -> "application/json"
      )
      .execute[SdesNotificationResult](SdesNotificationHttpReads, ec)

}




