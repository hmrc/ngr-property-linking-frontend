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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

case class ManualPropertySearchParams(
                                       postcode: String,
                                       addressLine1: Option[String] = None,
                                       addressLine2: Option[String] = None,
                                       town: Option[String] = None,
                                       propertyReference: Option[String] = None,
                                       miniRateableValue: Option[Long] = None,
                                       maxRateableValue: Option[Long] = None
                                     ) {

  private def encode(value: String): String =
    URLEncoder.encode(value, UTF_8.toString)

  private def clean(value: String): String =
    value.replaceAll("['()]", "")

  def toQueryString: String = {
    val base = s"postcode=${encode(postcode)}"

    val optionalParams = Seq(
      "propertyNameNumber"      -> addressLine1.map(clean),
      "street"                  -> addressLine2.map(clean),
      "town"                    -> town,
      "localAuthorityReference" -> propertyReference,
      "fromRateableValue"       -> miniRateableValue.map(_.toString),
      "toRateableValue"         -> maxRateableValue.map(_.toString)
    )

    val extras = optionalParams.collect {
      case (key, Some(value)) => s"$key=${encode(value)}"
    }

    (base +: extras).mkString("&")
  }

  def toUrl(baseUrl: String): String =
    s"$baseUrl/external-ndr-list-api/properties?${toQueryString}"
}

