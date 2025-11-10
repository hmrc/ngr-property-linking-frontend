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

import play.api.http.Status
import play.api.http.Status.ACCEPTED
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.ErrorResponse
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers

import java.net.URI
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NgrNotifyConnector @Inject()(
                                               http: HttpClientV2,
                                               appConfig: AppConfig
                                             )(implicit ec: ExecutionContext) {

  private def uri(path: String) = new URI(s"${appConfig.ngrNotify}/$path")

  def postProperty(propertyLinkingUserAnswers: PropertyLinkingUserAnswers)
                       (implicit hc: HeaderCarrier): Future[Either[ErrorResponse, HttpResponse]] = {
    http.post(uri("property").toURL)
      .withBody(Json.toJson(propertyLinkingUserAnswers))
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case ACCEPTED => Right(response)
          case _ => Left(ErrorResponse(response.status, response.body))
        }
      } recover {
      case ex =>
        Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, s"Call to ngr-notify property endpoint failed: ${ex.getMessage}"))
    }
  }
}