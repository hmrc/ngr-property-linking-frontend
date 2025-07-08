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

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.http.BadRequestException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.AuthenticatedUserRequest
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.SingleSearchResultForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.{LookUpVMVProperties, VMVProperty}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{ErrorTemplate, SingleSearchResultView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.paginate.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FindAPropertyRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.SortingVMVPropertiesService

import java.text.NumberFormat
import java.util.Locale
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SingleSearchResultController @Inject(singleSearchResultView: SingleSearchResultView,
                                           errorView: ErrorTemplate,
                                           authenticate: AuthRetrievals,
                                           findAPropertyRepo: FindAPropertyRepo,
                                           isRegisteredCheck: RegistrationAction,
                                           sortingVMVPropertiesService: SortingVMVPropertiesService,
                                           mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private lazy val defaultPageSize: Int = 10

  def selectedProperty(index: Int): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck) async { _ =>
      Future.successful(Redirect(routes.PropertySelectedController.show(index)))
    }
  }
  
  def show(page: Option[Int], sortBy: Option[String]): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      findAPropertyRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
        case Some(properties) =>
          showSingleSearchResultView(properties, page.getOrElse(1), sortBy.getOrElse("AddressASC"))
        case None => Future.successful(Redirect(routes.FindAPropertyController.show))
      }
    }

  def sort: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.failed(new BadRequestException("Unable to sort, please try again"))
          ,
          singleSearchResult => {
            findAPropertyRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
              case Some(properties) => showSingleSearchResultView(properties, 1, singleSearchResult.sortBy)
              case None => Future.failed(new BadRequestException("Unable to sort, please try again"))
            }
          }
        )
    }

  private def generateSortingSelectItems(selectedValue: String)(implicit messages: Messages): Seq[SelectItem] =
    (1 until 9).map(index =>
      val value: String = messages(s"singleSearchResultPage.sortBy.item$index.value")
      SelectItem(
        value = Some(value),
        text = messages(s"singleSearchResultPage.sortBy.item$index"),
        selected = value.equals(selectedValue)
      )
    )

  private def showSingleSearchResultView(properties: LookUpVMVProperties, currentPage: Int, sortBy: String)(implicit messages: Messages, request: AuthenticatedUserRequest[AnyContent]): Future[Result] =
    val totalProperties = properties.vmvProperties.total
    val pageSize = defaultPageSize
    val pageTop = PaginationData.pageTop(currentPage = currentPage, pageSize = pageSize, totalLength = totalProperties)
    val pageBottom = PaginationData.pageBottom(currentPage = currentPage, pageSize = pageSize) + (if (pageTop == 0) 0 else 1)
    val sortedVMVProperties: List[VMVProperty] = sortingVMVPropertiesService.sort(properties.vmvProperties.properties, sortBy)

    def totalPages: Int = math.ceil(properties.vmvProperties.properties.length.toFloat / defaultPageSize.toFloat).toInt

    def zipWithIndex(currentPage: Int, pageSize: Int, address: Seq[VMVProperty]): Seq[(VMVProperty, String)] = {
      val url = (i: Int) => if (currentPage > 1) {
        routes.SingleSearchResultController.selectedProperty(i + defaultPageSize).url
      } else {
        routes.SingleSearchResultController.selectedProperty(i).url
      }
      splitAddressByPage(currentPage, pageSize, address).zipWithIndex.map(x => (x._1, url(x._2)))
    }

    def splitAddressByPage(currentPage: Int, pageSize: Int, address: Seq[VMVProperty]): Seq[VMVProperty] = {
      PaginationData.getPage(currentPage = currentPage, pageSize = pageSize, list = address)
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
          TableHeader("Relatable Value", "govuk-table__caption--s"),
          TableHeader("", "")),
        rows = zipWithIndex(currentPage, defaultPageSize, propertyList)
          .map(stringValue => Seq(
            TableRowText(capitalizeEnds(stringValue._1.addressFull)),
            TableRowText(stringValue._1.localAuthorityReference),
            TableRowText(stringValue._1.valuations.last.descriptionText.toLowerCase.capitalize),
            TableRowText(formatRateableValue(stringValue._1.valuations.last.rateableValue.map(_.longValue).getOrElse(0l))),
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
        form = form,
        navigationBarContent = createDefaultNavBar,
        searchAgainUrl = routes.FindAPropertyController.show.url,
        postcode = properties.vmvProperties.properties.head.addressFull.takeRight(8),
        totalProperties = totalProperties,
        pageTop = pageTop,
        pageBottom = pageBottom,
        paginationData = PaginationData(totalPages = totalPages, currentPage = currentPage, baseUrl = "/ngr-property-linking-frontend/results", pageSize = defaultPageSize, sortBy = sortBy),
        propertySearchResultTable = generateTable(sortedVMVProperties),
        sortingSelectItems = generateSortingSelectItems(sortBy)
      ))
    )
}
