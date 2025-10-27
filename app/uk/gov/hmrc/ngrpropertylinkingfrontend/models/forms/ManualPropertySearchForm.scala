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

import play.api.data.Form
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.validation.Constraint
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Postcode, ScatCode}

import java.text.NumberFormat
import java.util.Locale

final case class ManualPropertySearchForm(addressLine1: Option[String] = None,
                                          addressLine2: Option[String] = None,
                                          town: Option[String] = None,
                                          county: Option[String] = None,
                                          postcode: Option[Postcode] = None,
                                          propertyReference: Option[String] = None,
                                          council: Option[String] = None,
                                          scatCode: Option[ScatCode] = None,
                                          descriptionCode: Option[String] = None,
                                          miniRateableValue: Option[Long] = None,
                                          maxRateableValue: Option[Long] = None)

object ManualPropertySearchForm extends CommonFormValidators {
  implicit val format:OFormat[ManualPropertySearchForm] = Json.format[ManualPropertySearchForm]

  val formatter = NumberFormat.getInstance(Locale.getDefault)
  private val maxLineLength: Int = 100

  def unapply(manualPropertySearchForm: ManualPropertySearchForm) =
    Some(manualPropertySearchForm.addressLine1,
      manualPropertySearchForm.addressLine2,
      manualPropertySearchForm.town,
      manualPropertySearchForm.county,
      manualPropertySearchForm.postcode,
      manualPropertySearchForm.propertyReference,
      manualPropertySearchForm.council,
      manualPropertySearchForm.scatCode,
      manualPropertySearchForm.descriptionCode,
      manualPropertySearchForm.miniRateableValue,
      manualPropertySearchForm.maxRateableValue)

  private def roundUpIntoLong(value: String): Long = {
     math.round(value.replaceAll("[£|,|\\s]", "").toDouble)
  }
  
  def form: Form[ManualPropertySearchForm] = {
    Form(
      mapping(
        "addressLine1" -> optional(
          text()
          .verifying(
            firstError(
              maxLength(maxLineLength, "manualSearchProperty.line1.maxLength.error")
            )
          )
        ),
        "addressLine2" -> optional(
          text()
            .verifying(
              firstError(
                maxLength(maxLineLength, "manualSearchProperty.line2.maxLength.error")
              )
            )
        ),
        "town" -> optional(
          text()
          .verifying(
            firstError(
              maxLength(maxLineLength, "manualSearchProperty.city.maxLength.error")
            )
          )
        ),
        "county" -> optional(
          text()
            .verifying(
              firstError(
                maxLength(maxLineLength, "manualSearchProperty.county.maxLength.error")
              )
            )
        ),
        "postcode" -> optional(
          text()
            .transform[String](_.strip(), identity)
            .verifying(
              firstError(
                regexp(postcodeRegexPattern.pattern(), "manualSearchProperty.postcode.invalid.error")
              )
            )
            .transform[Postcode](Postcode.apply, _.value)
        ),
        "propertyReference" -> optional(
          text()
            .verifying(maxLength(100, "manualSearchProperty.propertyReference.maxLength.error"))
        ),
        "council" -> optional(
          text()
        ),
        "scatCode" -> optional(
          text()
            .transform[Int](_.toInt, _.toString)
            .transform[ScatCode](ScatCode.apply, _.value)
        ),
        "descriptionCode" -> optional(
          text()
        ),
        "miniRateableValue" -> optional(
          text()
            .transform[String](_.strip(), identity)
            .verifying(
              maxLength(20, "manualSearchProperty.miniRateableValue.maxLength.error"),
              regexp(rateableValuePattern.pattern(), "manualSearchProperty.miniRateableValue.invalid.error")
            )
            .transform[Long](roundUpIntoLong, _.toString)
            .verifying(
              maximumValue[Long](2147483647, "manualSearchProperty.miniRateableValue.limit.error")
            )
        ),
        "maxRateableValue" -> optional(
          text()
            .verifying(
              maxLength(20, "manualSearchProperty.maxRateableValue.maxLength.error"),
              regexp(rateableValuePattern.pattern(), "manualSearchProperty.maxRateableValue.invalid.error")
            )
            .transform[Long](roundUpIntoLong, _.toString)
            .verifying(
              maximumValue[Long](2147483647, "manualSearchProperty.maxRateableValue.limit.error")
            )
        )
      )(ManualPropertySearchForm.apply)(ManualPropertySearchForm.unapply)
        .verifying("manualSearchProperty.miniRateableValue.validation.error",
          manualPropertySearch => {
            val minValue: Long = manualPropertySearch.miniRateableValue.getOrElse(0)
            val maxValue: Long = manualPropertySearch.maxRateableValue.getOrElse(0)
            if (manualPropertySearch.maxRateableValue.isDefined)
              minValue < maxValue
            else
              true
          })
    )
  }
}

