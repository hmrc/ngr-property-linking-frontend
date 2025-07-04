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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties

import play.api.libs.json.{Format, Json, OFormat}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId

case class VMVProperties(total: Int, properties: List[VMVProperty])

object VMVProperties {
  implicit val format: Format[VMVProperties] = Json.format[VMVProperties]
}

case class VMVProperty(uarn: Long,
                       addressFull: String,
                       localAuthorityCode: String,
                       localAuthorityReference: String,
                       valuations: List[Valuation],
                      )

object VMVProperty {
  implicit val format: OFormat[VMVProperty] = Json.format[VMVProperty]
}

case class LookUpVMVProperties(credId: CredId, vmvProperties: VMVProperties)

object LookUpVMVProperties {
  implicit val format: Format[LookUpVMVProperties] = Json.format[LookUpVMVProperties]
}