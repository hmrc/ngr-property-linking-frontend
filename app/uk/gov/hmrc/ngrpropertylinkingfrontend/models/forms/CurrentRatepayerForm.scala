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

import play.api.data.{Form, Mapping}
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError, ValidationResult}
import play.api.libs.json.{Json, OFormat}

import scala.util.Try
import java.time.LocalDate

final case class CurrentRatepayerForm(radioValue: String, day: Option[String], month: Option[String], year: Option[String])

object CurrentRatepayerForm extends CommonFormValidators {
  implicit val format: OFormat[CurrentRatepayerForm] = Json.format[CurrentRatepayerForm]

  private val radioUnselectedError = "currentRatepayer.radio.unselected.error"
  private val currentRatepayerRadio       = "current-ratepayer-radio"
  
  def unapply(currentRatepayerForm: CurrentRatepayerForm): Option[(String, Option[String], Option[String], Option[String])] =
    Some(currentRatepayerForm.radioValue,
      currentRatepayerForm.day,
      currentRatepayerForm.month,
      currentRatepayerForm.year)

  private def areDayMonthYearEntered(currentRatepayer: CurrentRatepayerForm): Boolean  =
    currentRatepayer.radioValue.equals("After") && currentRatepayer.day.nonEmpty && currentRatepayer.month.nonEmpty && currentRatepayer.year.nonEmpty

  private def isDayMonthOrYearEntered(currentRatepayer: CurrentRatepayerForm): Boolean =
    currentRatepayer.radioValue.equals("After") && currentRatepayer.day.nonEmpty || currentRatepayer.month.nonEmpty || currentRatepayer.year.nonEmpty

  private def isDateEmpty(currentRatepayer: CurrentRatepayerForm): Boolean =
    currentRatepayer.radioValue.equals("After") && currentRatepayer.day.isEmpty && currentRatepayer.month.isEmpty && currentRatepayer.year.isEmpty

  private def getLocalDate(currentRatepayer: CurrentRatepayerForm): LocalDate =
    val day = currentRatepayer.day.get.toInt
    val month = currentRatepayer.month.get.toInt
    val year = currentRatepayer.year.get.toInt
    LocalDate.of(year, month, day)

  private def isDateNonEmpty[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (isDateEmpty(currentRatepayer))
        Invalid("currentRatepayer.date.empty.error")
      else
        Valid
    )

  private def isFieldNonEmpty[A](fieldName: String): Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (isDayMonthOrYearEntered(currentRatepayer))
        fieldName match
          case "day" => if (currentRatepayer.day.nonEmpty) Valid else Invalid("currentRatepayer.day.empty.error")
          case "month" => if (currentRatepayer.month.nonEmpty) Valid else Invalid("currentRatepayer.month.empty.error")
          case "year" => if (currentRatepayer.year.nonEmpty) Valid else Invalid("currentRatepayer.year.empty.error")
      else
        Valid
  )

  private def isFieldInvalid[A](fieldName: String): Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      def dayValidation =
        if (currentRatepayer.day.isEmpty || !dayOrMonthRegexPattern.matcher(currentRatepayer.day.get).matches())
          Invalid("currentRatepayer.day.format.error", dayOrMonthRegexPattern.pattern())
        else if (currentRatepayer.day.get.toInt < 1 || currentRatepayer.day.get.toInt > 31)
          Invalid("currentRatepayer.day.format.error", 1, 31)
        else
          Valid

      def monthValidation =
        if (currentRatepayer.month.isEmpty || !dayOrMonthRegexPattern.matcher(currentRatepayer.month.get).matches())
          Invalid("currentRatepayer.month.format.error", dayOrMonthRegexPattern.pattern())
        else if (currentRatepayer.month.get.toInt < 1 || currentRatepayer.month.get.toInt > 12)
          Invalid("currentRatepayer.month.format.error", 1, 12)
        else
          Valid

      def yearValidation =
        if (currentRatepayer.year.isEmpty || !yearRegexPattern.matcher(currentRatepayer.year.get).matches())
          Invalid("currentRatepayer.year.format.error", yearRegexPattern.pattern())
        else
          Valid

      if (isDayMonthOrYearEntered(currentRatepayer))
        fieldName match
          case "day" => dayValidation
          case "month" => monthValidation
          case "year" => yearValidation
      else
        Valid
    )

  private def isDateValid[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (areDayMonthYearEntered(currentRatepayer) && Try(getLocalDate(currentRatepayer)).isFailure)
          Invalid("currentRatepayer.date.format.error")
      else
        Valid
    )

  private def isDateBetween1stApril2026AndToday[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (areDayMonthYearEntered(currentRatepayer) && Try(getLocalDate(currentRatepayer)).isSuccess) {
        val date = getLocalDate(currentRatepayer)
        val firstAprilDate = LocalDate.of(2026, 4, 1)
        if (date.isBefore(firstAprilDate) || date.isAfter(LocalDate.now()))
          Invalid("currentRatepayer.date.invalid.error")
        else
          Valid
      } else {
        Valid
      }
    )

  def form: Form[CurrentRatepayerForm] = {
    Form(
      mapping(
        currentRatepayerRadio -> text()
          .verifying(isNotEmpty(currentRatepayerRadio, radioUnselectedError)),
        "day" -> optional(text()),
        "month" -> optional(text()),
        "year" -> optional(text())
      )(CurrentRatepayerForm.apply)(CurrentRatepayerForm.unapply)
        .verifying(
          firstError(
            isFieldNonEmpty("day"),
            isFieldInvalid("day")
          )
        )
        .verifying(
          firstError(
            isFieldNonEmpty("month"),
            isFieldInvalid("month")
          )
        )
        .verifying(
          firstError(
            isFieldNonEmpty("year"),
            isFieldInvalid("year"),
            isDateValid
          )
        )
        .verifying(
          firstError(
            isDateNonEmpty,
            isDateBetween1stApril2026AndToday
          )
        )
    )
  }

}



