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
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavigationBarContent
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyRequestSentView

class AddPropertyRequestSentViewSpec extends ViewBaseSpec {
  lazy val view: AddPropertyRequestSentView = inject[AddPropertyRequestSentView]
  lazy val navBarContent: NavigationBarContent = createDefaultNavBar()
  lazy val summaryList: SummaryList = SummaryList(Seq(
    NGRSummaryListRow(messages("Address"), None, Seq("123 Nice Lane"), None),
    NGRSummaryListRow(messages("Property Reference"), None, Seq("123456789"), None)
  ).map(summarise))

  object Selectors {
    val title = "#main-content > div > div > div.govuk-panel.govuk-panel--confirmation > h1"
    val yourRef = "#main-content > div > div > div.govuk-panel.govuk-panel--confirmation > div"
    val print = "#printPage > a"
    val emailSent = "#main-content > div > div > p:nth-child(5)"
    val whatNext = "#main-content > div > div > h2"
    val p1 = "#main-content > div > div > p:nth-child(7)"
    val p2 = "#main-content > div > div > p:nth-child(8)"
    val goBack = "#main-content > div > div > p:nth-child(9) > a"
  }

  "AddPropertyRequestSent" must {
    val AddPropertyRequestSentView = view("ref", summaryList, navBarContent)
    lazy implicit val document: Document = Jsoup.parse(AddPropertyRequestSentView.body)
    val htmlApply = view.apply("ref", summaryList, navBarContent).body
    val htmlRender = view.render("ref", summaryList, navBarContent, request, messages, mockConfig).body
    lazy val htmlF = view.f("ref", summaryList, navBarContent)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "display correct content" in {

      elementText(Selectors.title) mustBe "Add a property request sent"
      elementText(Selectors.yourRef) must include("Your reference is")
      elementText(Selectors.print) mustBe "Print or save this page"
      elementText(Selectors.emailSent) mustBe "We have sent a confirmation email to"
      elementText(Selectors.whatNext) mustBe "What happens next"
      elementText(Selectors.p1) mustBe "We can usually give you a decision on your request to add a property within 15 working days. We will email you with our decision"
      elementText(Selectors.p2) mustBe "When we approve your request, you can report a change to your property, rent or lease."
      elementText(Selectors.goBack) mustBe "Go to your account home"
    }
  }
}
