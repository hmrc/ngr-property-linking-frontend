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
import play.api.data.Forms.{mapping, optional}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.mappings.Mappings
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.DateUtils

import java.time.LocalDate
import scala.util.Try

final case class UploadEvidenceForm(radioValue: String)

object UploadEvidenceForm extends CommonFormValidators with DateMappings with Mappings {
  implicit val format: OFormat[UploadEvidenceForm] = Json.format[UploadEvidenceForm]

  private val radioUnselectedError = "uploadEvidence.radio.unselected.error"
  private val uploadEvidenceRadio = "upload-evidence-radio"
  
  def unapply(currentRatepayerForm: UploadEvidenceForm): Option[String] = Some(currentRatepayerForm.radioValue)

  def form(implicit appConfig: AppConfig): Form[UploadEvidenceForm] = {
    Form(
      mapping(
        uploadEvidenceRadio -> text(radioUnselectedError)
      )(UploadEvidenceForm.apply)(UploadEvidenceForm.unapply)
    )
  }

}



