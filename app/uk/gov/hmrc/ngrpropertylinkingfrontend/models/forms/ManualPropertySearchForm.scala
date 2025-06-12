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
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Postcode

final case class ManualPropertySearchForm(line1: String,
                                          line2: Option[String],
                                          town: String,
                                          county: Option[String],
                                          postcode: Postcode)

object ManualPropertySearchForm extends CommonFormValidators {
  implicit val format:OFormat[ManualPropertySearchForm] = Json.format[ManualPropertySearchForm]

  private val maxLineLength: Int = 100

  def unapply(manualPropertySearchForm: ManualPropertySearchForm): Option[(String, Option[String], String, Option[String], Postcode)] =
    Some(manualPropertySearchForm.line1, manualPropertySearchForm.line2, manualPropertySearchForm.town, manualPropertySearchForm.county, manualPropertySearchForm.postcode)

  def form: Form[ManualPropertySearchForm] = {
    Form(
      mapping(
        "addressLine1" -> text()
          .verifying(
            firstError(
              isNotEmpty("addressLine1", "manualSearchProperty.line1.required.error"),
              maxLength(maxLineLength, "manualSearchProperty.line1.maxLength.error")
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
        "city" -> text()
          .verifying(
            firstError(
              isNotEmpty("city", "manualSearchProperty.city.required.error"),
              maxLength(maxLineLength, "manualSearchProperty.city.maxLength.error")
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
        "postcode" ->
          text()
            .transform[String](_.strip(), identity)
            .verifying(
              firstError(
                isNotEmpty("postcode", "manualSearchProperty.postcode.required.error"),
                regexp(postcodeRegexPattern.pattern(), "manualSearchProperty.postcode.invalid.error")
              )
            ) .transform[Postcode](Postcode.apply, _.value)
      )(ManualPropertySearchForm.apply)(ManualPropertySearchForm.unapply)
    )
  }
}

