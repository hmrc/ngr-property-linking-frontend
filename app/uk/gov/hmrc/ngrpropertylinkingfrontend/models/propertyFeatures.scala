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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import play.api.libs.json.*

//Add features here with type
sealed trait FeatureKey[A] {
  def name: String
}

case object Rooms extends FeatureKey[Int]        { val name = "Rooms" }
case object HasGarage extends FeatureKey[Boolean]{ val name = "HasGarage" }

case class FeatureMap(entries: Map[String, JsValue]) {
  def get[A](key: FeatureKey[A])(implicit reads: Reads[A]): Option[A] =
    entries.get(key.name).flatMap(_.validate[A].asOpt)

  def add[A](key: FeatureKey[A], value: A)(implicit writes: Writes[A]): FeatureMap =
    this.copy(entries + (key.name -> Json.toJson(value)))
}

object FeatureMap {
  def empty: FeatureMap = FeatureMap(Map.empty)

  implicit val format: Format[FeatureMap] = Format(
    Reads(json => json.validate[Map[String, JsValue]].map(FeatureMap(_))),
    Writes(fm => Json.toJson(fm.entries))
  )
}

