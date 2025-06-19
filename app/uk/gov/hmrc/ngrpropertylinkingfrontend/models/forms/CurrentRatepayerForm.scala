/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.libs.json.{Json, OFormat}

final case class CurrentRatepayerForm(radioValue: String, day: Option[String], month: Option[Int], year: Option[Int])

object CurrentRatepayerForm extends CommonFormValidators {
  implicit val format: OFormat[CurrentRatepayerForm] = Json.format[CurrentRatepayerForm]

  private lazy val radioUnselectedError = "currentRatepayer.radio.unselected.error"
  private val currentRatepayerRadio       = "current-ratepayer-radio"
  
  def unapply(currentRatepayerForm: CurrentRatepayerForm): Option[(String, Option[String], Option[Int], Option[Int])] =
    Some(currentRatepayerForm.radioValue,
      currentRatepayerForm.day,
      currentRatepayerForm.month,
      currentRatepayerForm.year)
  
  def form: Form[CurrentRatepayerForm] = {
    Form(
      mapping(
        currentRatepayerRadio -> text()
          .verifying(isNotEmpty(currentRatepayerRadio, radioUnselectedError)),
        "day" -> optional(
          text()
            .verifying(
              regexp(dayOrMonthRegexPattern.pattern(), "currentRatepayer.date.format.error")
            )
//            .transform[Int](_.toInt, _.toString)
        ),
        "month" -> optional(
          text()
            .verifying(
              regexp(dayOrMonthRegexPattern.pattern(), "currentRatepayer.date.format.error")
            )
            .transform[Int](_.toInt, _.toString)
        ),
        "year" -> optional(
          text()
            .verifying(
              regexp(dayOrMonthRegexPattern.pattern(), "currentRatepayer.date.format.error")
            )
            .transform[Int](_.toInt, _.toString)
        )
      )(CurrentRatepayerForm.apply)(CurrentRatepayerForm.unapply)
    )
  }

}



