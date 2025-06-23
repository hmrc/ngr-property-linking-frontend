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

import java.time.{DateTimeException, LocalDate}

final case class CurrentRatepayerForm(radioValue: String, day: Option[Int], month: Option[Int], year: Option[Int])

object CurrentRatepayerForm extends CommonFormValidators {
  implicit val format: OFormat[CurrentRatepayerForm] = Json.format[CurrentRatepayerForm]

  private lazy val radioUnselectedError = "currentRatepayer.radio.unselected.error"
  private val currentRatepayerRadio       = "current-ratepayer-radio"
  
  def unapply(currentRatepayerForm: CurrentRatepayerForm): Option[(String, Option[Int], Option[Int], Option[Int])] =
    Some(currentRatepayerForm.radioValue,
      currentRatepayerForm.day,
      currentRatepayerForm.month,
      currentRatepayerForm.year)

  private def areDayMonthYearEntered(currentRatepayer: CurrentRatepayerForm): Boolean  =
    currentRatepayer.day.nonEmpty && currentRatepayer.month.nonEmpty && currentRatepayer.year.nonEmpty

  private def isDateValid(currentRatepayer: CurrentRatepayerForm): Boolean =
    if (currentRatepayer.radioValue.equals("After") && areDayMonthYearEntered(currentRatepayer)) {
      val day = currentRatepayer.day.get
      val month = currentRatepayer.month.get
      val year = currentRatepayer.year.get
      try {
        LocalDate.of(year, month, day)
        true
      } catch {
        case e: DateTimeException => false
      }
    } else {
      true
    }

  private def isDateBetween1stApril2026AndToday(currentRatepayer: CurrentRatepayerForm): Boolean =
    if (currentRatepayer.radioValue.equals("After")  && areDayMonthYearEntered(currentRatepayer)) {
      val day = currentRatepayer.day.get
      val month = currentRatepayer.month.get
      val year = currentRatepayer.year.get
      try {
        val date = LocalDate.of(year, month, day)
        val firstAprilDate = LocalDate.of(2025, 3, 31)
        date.isAfter(firstAprilDate) && date.isBefore(LocalDate.now().plusDays(1))
      } catch {
        case e: DateTimeException => false
      }
    } else {
      true
    }

  private def isFieldNonEmpty(currentRatepayer: CurrentRatepayerForm, fieldName: String): Boolean =
    if (currentRatepayer.radioValue.equals("After") && currentRatepayer.day.nonEmpty || currentRatepayer.month.nonEmpty || currentRatepayer.year.nonEmpty) {
      fieldName match
        case "day" => currentRatepayer.day.nonEmpty
        case "month" => currentRatepayer.month.nonEmpty
        case "year" => currentRatepayer.year.nonEmpty
    } else {
      true
    }

  private def isDateNonEmpty(currentRatepayer: CurrentRatepayerForm): Boolean =
    if (currentRatepayer.radioValue.equals("After")) {
      currentRatepayer.day.nonEmpty || currentRatepayer.month.nonEmpty || currentRatepayer.year.nonEmpty
    } else {
      true
    }
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
            .transform[Int](_.toInt, _.toString)
            .verifying(
              inRange(1, 31, "currentRatepayer.date.format.error")
            )
        ),
        "month" -> optional(
          text()
            .verifying(
              regexp(dayOrMonthRegexPattern.pattern(), "currentRatepayer.date.format.error")
            )
            .transform[Int](_.toInt, _.toString)
            .verifying(
              inRange(1, 12, "currentRatepayer.date.format.error")
            )
        ),
        "year" -> optional(
          text()
            .verifying(
              regexp(yearRegexPattern.pattern(), "currentRatepayer.date.format.error")
            )
            .transform[Int](_.toInt, _.toString)
        )
      )(CurrentRatepayerForm.apply)(CurrentRatepayerForm.unapply)
        .verifying("currentRatepayer.date.empty.error", currentRatepayer => isDateNonEmpty(currentRatepayer))
        .verifying("currentRatepayer.day.empty.error", currentRatepayer => isFieldNonEmpty(currentRatepayer, "day"))
        .verifying("currentRatepayer.month.empty.error", currentRatepayer => isFieldNonEmpty(currentRatepayer, "month"))
        .verifying("currentRatepayer.year.empty.error", currentRatepayer => isFieldNonEmpty(currentRatepayer, "year"))
        .verifying("currentRatepayer.date.format.error", currentRatepayer => isDateValid(currentRatepayer))
        .verifying("currentRatepayer.date.invalid.error", currentRatepayer => isDateBetween1stApril2026AndToday(currentRatepayer))
    )
  }

}



