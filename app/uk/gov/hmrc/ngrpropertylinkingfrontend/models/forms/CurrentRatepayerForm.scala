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
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Json, OFormat}

final case class CurrentRatepayerForm(radioValue: String)

object CurrentRatepayerForm extends CommonFormValidators {
  implicit val format: OFormat[CurrentRatepayerForm] = Json.format[CurrentRatepayerForm]

  private lazy val radioUnselectedError = "confirmAddress.radio.unselected.error"
  private val confirmAddressRadio       = "confirm-address-radio"
  
  def unapply(currentRatepayerForm: CurrentRatepayerForm): Option[String] = Some(CurrentRatepayerForm.confirmAddressRadio)
  
  def form: Form[CurrentRatepayerForm] = {
    Form(
      mapping(
        confirmAddressRadio -> text()
          .verifying(isNotEmpty(confirmAddressRadio, radioUnselectedError))
      )(CurrentRatepayerForm.apply)(CurrentRatepayerForm.unapply)
    )
  }

}



