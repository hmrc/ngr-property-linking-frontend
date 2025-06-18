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

import play.api.libs.json.Json
import play.api.libs.ws.writeableOf_JsValue
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{UpscanInitiateResponse, UpscanInitiateRequest}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpscanConnector @Inject()(httpClientV2: HttpClientV2, appConfig: AppConfig)(implicit ec: ExecutionContext) {

  def initiate(implicit headerCarrier: HeaderCarrier): Future[UpscanInitiateResponse] = {
    val upscanInitiateUri = s"${appConfig.upscanHost}/upscan/v2/initiate"
    //TODO finalise these fields
    val request = UpscanInitiateRequest(
      callbackUrl = "http://localhost:1504/ngr-property-linking-frontend/callback-from-upscan",
      successRedirect = Some("http://localhost:1504/ngr-property-linking-frontend/uploaded-business-rates-bill"),
      //TODO do failure redirect
      errorRedirect = Some("https://failureRedirect.com"),
      maximumFileSize = Some(25000000))//25MB
    
    //TODO enter it into DB
    httpClientV2
      .post(url"$upscanInitiateUri")
      .withBody(Json.toJson(request))
      .setHeader(HeaderNames.CONTENT_TYPE -> "application/json")
      .execute[UpscanInitiateResponse]
  }

  def download(downloadUrl: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClientV2
      .get(url"$downloadUrl")
      .execute[HttpResponse]
}


