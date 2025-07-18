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

import play.api.data.{Form, FormError, Forms}
import play.api.data.Forms.single
import play.api.data.format.Formatter
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.RadioEntry

sealed trait ConnectionToPropertyForm extends RadioEntry

object ConnectionToPropertyForm {
  val formName = "connection-to-property-radio"

  case object Owner extends ConnectionToPropertyForm
  case object Occupier extends ConnectionToPropertyForm
  case object OwnerAndOccupier extends ConnectionToPropertyForm

  val values: Set[ConnectionToPropertyForm] = Set(Owner, Occupier, OwnerAndOccupier)

  def stringToPropertyForm(value: String): ConnectionToPropertyForm = value match {
    case "Owner" => Owner
    case "Occupier" => Occupier
    case "OwnerAndOccupier" => OwnerAndOccupier
    case _ => throw new IllegalArgumentException(s"Invalid ConnectionToPropertyForm input $value")
  }
  
  implicit val connectionToPropertyFormatter: Formatter[ConnectionToPropertyForm] = new Formatter[ConnectionToPropertyForm] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ConnectionToPropertyForm] = {
      data.get(key).collectFirst {
        case "Owner" => Owner
        case "Occupier" => Occupier
        case "OwnerAndOccupier" => OwnerAndOccupier
      }.toRight(Seq(FormError(key, "connectionToProperty.radio.unselected.error")))
    }

    override def unbind(key: String, value: ConnectionToPropertyForm): Map[String, String] = Map(
      key -> (value match {
        case Owner => "Owner"
        case Occupier => "Occupier"
        case OwnerAndOccupier => "OwnerAndOccupier"
      })
    )
  }

  def form: Form[ConnectionToPropertyForm] = Form(
      single(formName -> Forms.of[ConnectionToPropertyForm])
    )

}
