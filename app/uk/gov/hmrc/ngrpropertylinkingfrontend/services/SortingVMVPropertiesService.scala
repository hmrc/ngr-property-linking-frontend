/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.VMVProperty

import javax.inject.{Inject, Singleton}

@Singleton
class SortingVMVPropertiesService @Inject() {

  def sort(properties: List[VMVProperty], sortBy: String) = {
    sortBy match
      case "AddressASC"        => properties.sortBy(_.addressFull)
      case "AddressDESC"       => properties.sortBy(_.addressFull).reverse
      case "ReferenceASC"      => properties.sortBy(_.localAuthorityReference)
      case "ReferenceDESC"     => properties.sortBy(_.localAuthorityReference).reverse
      case "DescriptionASC"    => properties.sortBy(_.valuations.last.descriptionText)
      case "DescriptionDESC"   => properties.sortBy(_.valuations.last.descriptionText).reverse
      case "RateableValueASC"  => properties.sortBy(_.valuations.last.rateableValue.map(_.longValue).getOrElse(0l))
      case "RateableValueDESC" => properties.sortBy(_.valuations.last.rateableValue.map(_.longValue).getOrElse(0l)).reverse
  }
}
