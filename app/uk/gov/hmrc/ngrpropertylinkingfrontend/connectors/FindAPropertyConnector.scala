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
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.*
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.{FindAProperty, ManualPropertySearchForm}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.VMVProperties
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{ErrorResponse, ManualPropertySearchParams}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class OptionalParams(
                           propertyNameNumber:Option[String],
                           street:Option[String],
                           town:Option[String],
                           localAuthorityReference:Option[String],
                           fromRateableValue:Option[String],
                           toRateableValue:Option[String],
                         )

@Singleton
class FindAPropertyConnector @Inject()(
                                        http: HttpClientV2,
                                       appConfig: AppConfig,
                                       logger: NGRLogger)
                                      (implicit ec: ExecutionContext) {

  def findAPropertyManualSearch(searchParams: ManualPropertySearchForm)(implicit headerCarrier: HeaderCarrier): Future[Either[ErrorResponse, VMVProperties]] = {
    val urlEndpoint =  if(appConfig.features.vmvPropertyLookupTestEnabled()){
      url"${appConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=${searchParams.postcode.value.toUpperCase().take(4).trim.replaceAll("\\s", "")}"
    }else{
      val query = ManualPropertySearchParams(
        postcode = searchParams.postcode.value,
        addressLine1 = searchParams.addressLine1,
        addressLine2 = searchParams.addressLine2,
        town = searchParams.town,
        propertyReference = searchParams.propertyReference,
        miniRateableValue = searchParams.miniRateableValue,
        maxRateableValue = searchParams.maxRateableValue
      ).toUrl(appConfig.addressLookupUrl)

      url"$query"
    }
    http.get(urlEndpoint)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK | NOT_FOUND =>
            response.json.validate[VMVProperties] match {
              case JsSuccess(valid, _) =>
                logger.info(s"Successfully Received propertyList ${response.body}")
                Right(valid)
              case JsError(errors) =>
                logger.error(s"Error received from vmv find a property service: $errors")
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case _ =>
            logger.error(s"Error received from vmv find a property service: ${response.body}")
            Left(ErrorResponse(response.status, response.body))
        }
      } recover {
      case _ =>
        logger.error(s"Error received from vmv find a property service")
        Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, "Call to VMV find a property failed"))
    }
  }

  def findAPropertyPostcodeSearch(searchParams: FindAProperty)(implicit headerCarrier: HeaderCarrier): Future[Either[ErrorResponse, VMVProperties]] = {
    val urlEndpoint = if (appConfig.features.vmvPropertyLookupTestEnabled()) {
      url"${appConfig.ngrStubHost}/ngr-stub/external-ndr-list-api/properties?postcode=${searchParams.postcode.value.toUpperCase().take(4).trim.replaceAll("\\s", "")}"
    } else {
      if(searchParams.propertyName.nonEmpty){
        url"${appConfig.addressLookupUrl}/external-ndr-list-api/properties?postcode=${searchParams.postcode}&propertyNameNumber=${searchParams.propertyName.map(_.replaceAll("['()]", "")).getOrElse(None)}"
      }else{ url"${appConfig.addressLookupUrl}/external-ndr-list-api/properties?postcode=${searchParams.postcode}"}
    }
    http.get(urlEndpoint)
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case OK | NOT_FOUND =>
            response.json.validate[VMVProperties] match {
              case JsSuccess(valid, _) =>
                logger.info(s"Successfully Received propertyList ${response.body}")
                Right(valid)
              case JsError(errors) =>
                logger.error(s"Error received from vmv find a property service: $errors")
                Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error: $errors"))
            }
          case _ =>
            logger.error(s"Error received from vmv find a property service: ${response.body}")
            Left(ErrorResponse(response.status, response.body))
        }
      } recover {
      case _ =>
        logger.error(s"Error received from vmv find a property service")
        Left(ErrorResponse(Status.INTERNAL_SERVER_ERROR, "Call to VMV find a property failed"))
    }
  }
}