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

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.FindAPropertyConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.paginate.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.{VMVProperties, VMVProperty}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{ErrorTemplate, SingleSearchResultView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FindAPropertyRepo

import java.text.NumberFormat
import java.util.Locale
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SingleSearchResultController @Inject( singleSearchResultView: SingleSearchResultView,
                                            errorView: ErrorTemplate,
                                            authenticate: AuthRetrievals,
                                            findAPropertyRepo: FindAPropertyRepo,
                                            isRegisteredCheck: RegistrationAction,
                                            mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val defaultPageSize: Int = 10

  def selectedProperty(index: Int): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck) async { _ =>
      Future.successful(Redirect(routes.PropertySelectedController.show(index)))
    }
  }

  def show(page: Int = 1): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      findAPropertyRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
        case Some(properties) =>
          val postcode:String = properties.vmvProperties.properties.head.addressFull.takeRight(8)
              val totalProperties = properties.vmvProperties.total
              val currentPage = page
              val pageSize = defaultPageSize
              val pageTop = PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, totalLength = totalProperties)
              val pageBottom = PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + (if (pageTop == 0) 0 else 1)

              def totalPages: Int = math.ceil(properties.vmvProperties.properties.length.toFloat / defaultPageSize.toFloat).toInt

              def splitAddressByPage(currentPage: Int, pageSize: Int, address: Seq[VMVProperty]): Seq[VMVProperty] = {
                PaginationData.getPage(currentPage = currentPage, pageSize = pageSize, list = address)
              }

              def zipWithIndex(currentPage: Int, pageSize: Int, address: Seq[VMVProperty]): Seq[(VMVProperty, String)] = {
                val url = (i: Int) => if (page > 1) {
                  routes.SingleSearchResultController.selectedProperty(i + defaultPageSize).url
                } else {
                  routes.SingleSearchResultController.selectedProperty(i).url
                }
                splitAddressByPage(currentPage, pageSize, address).zipWithIndex.map(x => (x._1, url(x._2)))
              }

          def capitalizeEnds(input: String): String = {
            if (input.isEmpty) return input
            val lowerInput = input.toLowerCase
            val firstChar = lowerInput.head.toUpper
            val length = input.length
            val middle = if (length > 8) lowerInput.slice(1, length - 7) else ""
            val lastSeven = lowerInput.takeRight(7).toUpperCase
            s"$firstChar$middle$lastSeven"
          }

          def generateTable(propertyList: List[VMVProperty]): Table = {
            TableData(
              headers = Seq(
                TableHeader("Address", "govuk-table__caption--s"),
                TableHeader("Property reference", "govuk-table__caption--s"),
                TableHeader("Description", "govuk-table__caption--s"),
                TableHeader("Relatable Value", "govuk-table__caption--s")),
              rows = zipWithIndex(page, defaultPageSize, propertyList)
                .map(stringValue => Seq(
                  TableRowText(capitalizeEnds(stringValue._1.addressFull)),
                  TableRowText(stringValue._1.localAuthorityReference),
                  TableRowText(stringValue._1.valuations.last.descriptionText.toLowerCase.capitalize),
                  TableRowText(formatRateableValue(stringValue._1.valuations.last.rateableValue)),
                  TableRowLink(stringValue._2, "Select property")
                ))
            ).toTable
          }

          def formatRateableValue(rateableValue: Long): String = {
            val ukFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
            ukFormatter.format(rateableValue).replaceAll("[.][0-9]{2}", "")
          }


          Future.successful(
                Ok(singleSearchResultView(
                  navigationBarContent = createDefaultNavBar,
                  searchAgainUrl = routes.FindAPropertyController.show.url,
                  postcode = "BH1 7ST", // Check if this needs to remain as is
                  totalProperties = totalProperties,
                  pageTop = pageTop,
                  pageBottom = pageBottom,
                  paginationData = PaginationData(totalPages = totalPages, currentPage = page, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = defaultPageSize),
                  propertySearchResultTable = generateTable(properties.vmvProperties.properties),
                ))
              )

        case None => Future.failed(throw new Exception("Unable to find matching postcode"))
      }
    }
}
