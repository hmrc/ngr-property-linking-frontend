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
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.mappings.Mappings

import scala.util.Try
import java.time.LocalDate

final case class CurrentRatepayerForm(radioValue: String, maybeRatepayerDate: Option[RatepayerDate])

object CurrentRatepayerForm extends CommonFormValidators with Mappings {
  implicit val format: OFormat[CurrentRatepayerForm] = Json.format[CurrentRatepayerForm]

  private val radioUnselectedError = "currentRatepayer.radio.unselected.error"
  private val currentRatepayerRadio = "current-ratepayer-radio"
  
  def unapply(currentRatepayerForm: CurrentRatepayerForm): Option[(String, Option[RatepayerDate])] =
    Some(currentRatepayerForm.radioValue,
      currentRatepayerForm.maybeRatepayerDate)

  private def areDayMonthYearEntered(currentRatepayer: CurrentRatepayerForm): Boolean = {
    val maybeRatepayerDate: Option[RatepayerDate] = currentRatepayer.maybeRatepayerDate

    isDateDefined(currentRatepayer) && maybeRatepayerDate.get.day.nonEmpty &&
      maybeRatepayerDate.get.month.nonEmpty && maybeRatepayerDate.get.year.nonEmpty
  }

  private def isDayMonthOrYearEntered(currentRatepayer: CurrentRatepayerForm): Boolean = {
    val maybeRatepayerDate: Option[RatepayerDate] = currentRatepayer.maybeRatepayerDate

    isDateDefined(currentRatepayer) && (maybeRatepayerDate.get.day.nonEmpty ||
      maybeRatepayerDate.get.month.nonEmpty || maybeRatepayerDate.get.year.nonEmpty)
  }

  private def isDateEmpty(currentRatepayer: CurrentRatepayerForm): Boolean = {
    currentRatepayer.radioValue.equals("After") && currentRatepayer.maybeRatepayerDate.isEmpty
  }

  private def isDateDefined(currentRatepayer: CurrentRatepayerForm): Boolean = {
    currentRatepayer.radioValue.equals("After") && currentRatepayer.maybeRatepayerDate.nonEmpty
  }

  private def isDateDigits(ratepayerDate: RatepayerDate): Boolean = {
    Try(ratepayerDate.day.toInt).isSuccess &&
      Try(ratepayerDate.month.toInt).isSuccess &&
      Try(ratepayerDate.year.toInt).isSuccess
  }

  private def isDateNonEmpty[A]: Constraint[A] =
    Constraint ((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (isDateEmpty(currentRatepayer))
        Invalid("currentRatepayer.date.empty.error")
      else
        Valid
    )

  private def isFieldsNonEmpty[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (isDateDefined(currentRatepayer))
        val ratepayerDate = currentRatepayer.maybeRatepayerDate.get
        (ratepayerDate.day.isEmpty, ratepayerDate.month.isEmpty, ratepayerDate.year.isEmpty) match
          case (true, true, false)  => Invalid("currentRatepayer.day.month.empty.error")
          case (true, false, true)  => Invalid("currentRatepayer.day.year.empty.error")
          case (false, true, true)  => Invalid("currentRatepayer.month.year.empty.error")
          case (true, false, false) => Invalid("currentRatepayer.day.empty.error")
          case (false, true, false) => Invalid("currentRatepayer.month.empty.error")
          case (false, false, true) => Invalid("currentRatepayer.year.empty.error")
          case (_, _, _)            => Valid
      else
        Valid
  )

  private def areFieldsInvalid[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      def dayValidation(ratepayerDate: RatepayerDate): Option[ValidationError] =
        if (!dayOrMonthRegexPattern.matcher(ratepayerDate.day).matches())
          Some(ValidationError("currentRatepayer.day.format.error", dayOrMonthRegexPattern.pattern()))
        else if (ratepayerDate.day.toInt < 1 || ratepayerDate.day.toInt > 31)
          Some(ValidationError("currentRatepayer.day.format.error", 1, 31))
        else
          None

      def monthValidation(ratepayerDate: RatepayerDate): Option[ValidationError] =
        if (!dayOrMonthRegexPattern.matcher(ratepayerDate.month).matches())
          Some(ValidationError("currentRatepayer.month.format.error", dayOrMonthRegexPattern.pattern()))
        else if (ratepayerDate.month.toInt < 1 || ratepayerDate.month.toInt > 12)
          Some(ValidationError("currentRatepayer.month.format.error", 1, 12))
        else
          None

      def yearValidation(ratepayerDate: RatepayerDate): Option[ValidationError] =
        if (!yearRegexPattern.matcher(ratepayerDate.year).matches())
          Some(ValidationError("currentRatepayer.year.format.error", yearRegexPattern.pattern()))
        else
          None

      if (isDayMonthOrYearEntered(currentRatepayer))
        val ratepayerDate = currentRatepayer.maybeRatepayerDate.get
        val dayValidationError = if (ratepayerDate.day.nonEmpty) dayValidation(ratepayerDate) else None
        val monthValidationError = if (ratepayerDate.month.nonEmpty) monthValidation(ratepayerDate) else None
        val yearValidationError = if (ratepayerDate.year.nonEmpty) yearValidation(ratepayerDate) else None
        val validationErrors: Seq[ValidationError] = Seq(dayValidationError, monthValidationError, yearValidationError)
          .filterNot(_.isEmpty)
          .map(_.get)

        if (validationErrors.isEmpty) Valid else Invalid(validationErrors)
      else
        Valid
    )

  private def isDateValid[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (areDayMonthYearEntered(currentRatepayer) && isDateDigits(currentRatepayer.maybeRatepayerDate.get) &&
        Try(currentRatepayer.maybeRatepayerDate.get.ratepayerDate).isFailure)
          Invalid("currentRatepayer.date.format.error")
      else
        Valid
    )

  private def isDateBetween1stApril2026AndToday[A]: Constraint[A] =
    Constraint((input: A) =>
      val currentRatepayer = input.asInstanceOf[CurrentRatepayerForm]
      if (areDayMonthYearEntered(currentRatepayer) && Try(currentRatepayer.maybeRatepayerDate.get.ratepayerDate).isSuccess) {
        val date = currentRatepayer.maybeRatepayerDate.get.ratepayerDate
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
        currentRatepayerRadio ->
          text()
            .verifying(
              isNotEmpty(currentRatepayerRadio, radioUnselectedError)
            ),
        "ratepayerDate" -> optional(
          dateMapping
        )
      )(CurrentRatepayerForm.apply)(CurrentRatepayerForm.unapply)
        .verifying(
          firstError(
            isDateNonEmpty,
            isFieldsNonEmpty
          )
        )
        .verifying(
          firstError(
            areFieldsInvalid,
            isDateValid,
            isDateBetween1stApril2026AndToday
          )
        )
    )
  }

}



