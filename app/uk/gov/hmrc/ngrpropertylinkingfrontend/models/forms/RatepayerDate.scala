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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms

import play.api.data.Forms.{mapping, text}
import play.api.data.Mapping
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

final case class RatepayerDate(day: String, month: String, year: String) {
  lazy val ratepayerDate = LocalDate.of(year.toInt, month.toInt, day.toInt)
}

object RatepayerDate {
  implicit val format: OFormat[RatepayerDate] = Json.format[RatepayerDate]

  def unapply(ratepayerDate: RatepayerDate): Option[(String, String, String)] =
    Some(ratepayerDate.day, ratepayerDate.month, ratepayerDate.year)
}

trait Mappings extends CommonFormValidators {
  def dateMapping: Mapping[RatepayerDate] = {
    mapping(
      "day" -> text(),
      "month" -> text(),
      "year" -> text(),
    )(RatepayerDate.apply)(RatepayerDate.unapply)
  }
}