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

package uk.gov.hmrc.ngrpropertylinkingfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import uk.gov.hmrc.govukfrontend.views.Aliases.Table
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.SingleSearchResultForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.paginate.PaginationData
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.SingleSearchResultView

class SingleSearchResultViewSpec extends ViewBaseSpec {
  lazy val view: SingleSearchResultView = inject[SingleSearchResultView]
  val title = "Search results for BH1 7ST - GOV.UK"
  val heading = "Search results for BH1 7ST"
  val p1 = "Showing 1 to 1 of 1 items. Search again"
  val detailsSummary = "Help if you cannot find your property"
  val detailsP1 = "Check the details you entered are correct."
  val detailsP2 = "You can try different ways of searching for your property, for example by rateable value or property reference. Search again ."
  val detailsP3 = "Telephones:03000 501501(England)03000 505505(Wales / Cymru)"
  val openTime = "Opening times: Monday to Friday: 9:00am to 4:30pm"
  val searchLink = "Search again"
  val sortByLabel = "Sort by"
  val sortButton = "Sort"

  val searchUrl: String = routes.FindAPropertyController.show.url
  val dashboardUrl: String = mockConfig.ngrDashboardUrl

  val content: NavigationBarContent = NavBarPageContents.CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(homePage = true),
    notifications = Some(1)
  )

  val selectItems: Seq[SelectItem] =
    (1 until 9).map(index =>
      val value: String = messages(s"singleSearchResultPage.sortBy.item$index.value")
      SelectItem(
        value = Some(value),
        text = messages(s"singleSearchResultPage.sortBy.item$index")
      )
    )

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > form > div:nth-child(1) > div > h1"
    val p1 = "#main-content > div > div > form > div:nth-child(1) > div > p"
    val detailsSummary = "#help-if-you-cannot-find-your-property > summary"
    val detailsP1 = "#help-if-you-cannot-find-your-property > div > p:nth-child(1)"
    val detailsP2 = "#help-if-you-cannot-find-your-property > div > p:nth-child(4)"
    val opentime = "#help-if-you-cannot-find-your-property > div > p:nth-child(8)"
    val searchLink = "#main-content > div > div > form > div:nth-child(1) > div > p > a"
    val sortByLabel = "#main-content > div > div > form > div:nth-child(1) > div > div > label"
    val sortButton = "#continue"
  }

  "SingleSearchResultView" must {
    val form = SingleSearchResultForm
      .form
      .fillAndValidate(SingleSearchResultForm("AddressASC"))
    val noResultsFoundView = view(
      form,
      content,
      searchUrl,
      postcode = "BH1 7ST",
      totalProperties = 1,
      pageTop = 1,
      pageBottom = 1,
      paginationData = PaginationData(totalPages = 1, currentPage = 1, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = 10, sortBy = "AddressASC"),
      propertySearchResultTable = Table(),
      sortingSelectItems = selectItems)
    lazy implicit val document: Document = Jsoup.parse(noResultsFoundView.body)
    val htmlApply = view.apply(
      form,
      content,
      searchUrl,
      postcode = "BH1 7ST",
      totalProperties = 1,
      pageTop = 1,
      pageBottom = 1,
      paginationData = PaginationData(totalPages = 1, currentPage = 1, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = 10, sortBy = "AddressASC"),
      propertySearchResultTable = Table(),
      sortingSelectItems = selectItems).body
    val htmlRender = view.render(
      form,
      content,
      searchUrl,
      postcode = "BH1 7ST",
      totalProperties = 1,
      pageTop = 1,
      pageBottom = 1,
      paginationData = PaginationData(totalPages = 1, currentPage = 1, baseUrl = "/ngr-login-register-frontend/address-search-results", pageSize = 10, sortBy = "AddressASC"),
      propertySearchResultTable = Table(),
      sortingSelectItems = selectItems, request, messages, mockConfig).body

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "show correct title" in {
      elementText(Selectors.navTitle) mustBe title
    }

    "show correct heading" in {
      elementText(Selectors.heading) mustBe heading
    }

    "show correct p1" in {
      elementText(Selectors.p1) mustBe p1
    }

    "show correct details summary" in {
      elementText(Selectors.detailsSummary) mustBe detailsSummary
    }

    "show correct details description p1" in {
      elementText(Selectors.detailsP1) mustBe detailsP1
    }

    "show correct details description p2" in {
      elementText(Selectors.detailsP2) mustBe detailsP2
    }

    "show correct details open time" in {
      elementText(Selectors.opentime) mustBe openTime
    }

    "show correct search again link" in {
      elementText(Selectors.searchLink) mustBe searchLink
    }

    "show correct sort by select label" in {
      elementText(Selectors.sortByLabel) mustBe sortByLabel
    }

    "show correct sort button" in {
      elementText(Selectors.sortButton) mustBe sortButton
    }
  }
}
