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
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.NoResultsFoundView

class NoResultsFoundViewSpec extends ViewBaseSpec {
  lazy val view: NoResultsFoundView = inject[NoResultsFoundView]
  val title = "No results found - GOV.UK"
  val heading = "No results found"
  val p1 = "Check the detail you entered are correct."
  val p2 = "You can try different ways of searching for your property, for example by rateable value or council property reference. Search again ."
  val searchLink = "Search again"
  val returnHomeLink = "Return to account home"

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

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > div.govuk-grid-row > div > h1"
    val p1 = "#main-content > div > div > div.govuk-grid-row > div > p:nth-child(2)"
    val p2 = "#main-content > div > div > div.govuk-grid-row > div > p:nth-child(3)"
    val searchLink = "#main-content > div > div > div.govuk-grid-row > div > p:nth-child(3) > a"
    val returnHomeLink = "#main-content > div > div > div.govuk-grid-row > div > p:nth-child(4) > a"
  }

  "NoResultsFoundView" must {
    val noResultsFoundView = view(content, searchUrl, dashboardUrl)
    lazy implicit val document: Document = Jsoup.parse(noResultsFoundView.body)
    val htmlApply = view.apply(content, searchUrl, dashboardUrl).body
    val htmlRender = view.render(content, searchUrl, dashboardUrl, request, messages, mockConfig).body
    lazy val htmlF = view.f(content, searchUrl, dashboardUrl)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

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

    "show correct p2" in {
      elementText(Selectors.p2) mustBe p2
    }

    "show correct search link" in {
      elementText(Selectors.searchLink) mustBe searchLink
    }

    "show correct return home link" in {
      elementText(Selectors.returnHomeLink) mustBe returnHomeLink
    }
  }
}
