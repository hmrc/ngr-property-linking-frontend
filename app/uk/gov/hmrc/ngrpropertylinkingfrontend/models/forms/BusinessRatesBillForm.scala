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
import play.api.data.Forms.mapping
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.mappings.Mappings

final case class BusinessRatesBillForm(radioValue: String)

object BusinessRatesBillForm extends Mappings {
  implicit val format: OFormat[BusinessRatesBillForm] = Json.format[BusinessRatesBillForm]

  private lazy val radioUnselectedError = "businessRatesBill.error.required"
  private val businessRatesBillRadio    = "business-rates-bill-radio"
  
  def unapply(businessRatesBillForm: BusinessRatesBillForm): Option[String] = Some(businessRatesBillForm.radioValue)
  
  def form: Form[BusinessRatesBillForm] = {
    Form(
      mapping(
        businessRatesBillRadio -> text(radioUnselectedError)
      )(BusinessRatesBillForm.apply)(BusinessRatesBillForm.unapply)
    )
  }

}



