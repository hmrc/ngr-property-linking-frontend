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
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBaseSpec{
  lazy val view: CheckYourAnswersView = inject[CheckYourAnswersView]

  object Strings {
    val navTitle = "Check and confirm your details - GOV.UK"
    val heading = "Check and confirm your details"
    val summaryRow1 = "Property to add to account"
    val summaryRow2 = "Property reference"
    val summaryRow3 = "When did you become the current ratepayer?"
    val summaryRow4 = "Do you have a business rates bill for this property?"
    val summaryRow5 = "Evidence document"
    val summaryRow6 = "What is your connection to the property"
    val continue = "Continue"
  }

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > form > h1"
    val summaryRow1 = "#main-content > div > div > form > dl > div:nth-child(1) > dt"
    val summaryRow2 = "#main-content > div > div > form > dl > div:nth-child(2) > dt"
    val summaryRow3 = "#main-content > div > div > form > dl > div:nth-child(3) > dt"
    val summaryRow4 = "#main-content > div > div > form > dl > div:nth-child(4) > dt"
    val summaryRow5 = "#main-content > div > div > form > dl > div:nth-child(5) > dt"
    val summaryRow6 = "#main-content > div > div > form > dl > div:nth-child(6) > dt"
    val continue = "#continue"
  }

  val summary: Seq[SummaryListRow] = Seq(
    NGRSummaryListRow(
      messages("checkYourAnswers.property.title"),
      None,
      Seq("(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY"),
      changeLink = Some(Link(href = routes.FindAPropertyController.show, linkId = "property-address", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-address")))
    ),
    NGRSummaryListRow(
      messages("checkYourAnswers.currentRatepayer.title"),
      None,
      Seq("2191322564521"),
      None
    ),
    NGRSummaryListRow(
      messages("checkYourAnswers.currentRatepayer.title"),
      None,
      Seq("checkYourAnswers.currentRatepayer.before"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "current-ratepayer", messageKey = "service.change", visuallyHiddenMessageKey = Some("current-ratepayer")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.businessRatesBill"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "business-rates-bill", messageKey = "service.change", visuallyHiddenMessageKey = Some("business-rates-bill")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.EvidenceDocument"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "evidence-document", messageKey = "service.change", visuallyHiddenMessageKey = Some("evidence-document")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.PropertyConnection"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "property-connection", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-connection")))
    ) //TODO CHANGE CURRENT RATEPAYER
  ).map(summarise)


  private val councilUrl = "https://www.gov.uk/contact-your-local-council-about-business-rates"

  "AddPropertyToYourAccountView" must {
    val addPropertyToYourAccountView = view(content, SummaryList(summary))
    lazy implicit val document: Document = Jsoup.parse(addPropertyToYourAccountView.body)
    val htmlApply = view.apply(content, SummaryList(summary)).body
    val htmlRender = view.render(content, SummaryList(summary), request, messages, mockConfig).body
    lazy val htmlF = view.f(content, SummaryList(summary))

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
      elementText(Selectors.navTitle) mustBe Strings.navTitle
    }

    "show correct heading" in {
      elementText(Selectors.heading) mustBe Strings.heading
    }

    "summaryRow1" in {
      elementText(Selectors.summaryRow1) mustBe Strings.summaryRow1
    }

    "summaryRow2" in {
      elementText(Selectors.summaryRow2) mustBe Strings.summaryRow2
    }

    "summaryRow3" in {
      elementText(Selectors.summaryRow3) mustBe Strings.summaryRow3
    }

    "summaryRow4" in {
      elementText(Selectors.summaryRow4) mustBe Strings.summaryRow4
    }

    "summaryRow5" in {
      elementText(Selectors.summaryRow5) mustBe Strings.summaryRow5
    }

    "summaryRow6" in {
      elementText(Selectors.summaryRow6) mustBe Strings.summaryRow6
    }

    "show correct continue button" in {
      elementText(Selectors.continue) mustBe Strings.continue
    }
  }
}
