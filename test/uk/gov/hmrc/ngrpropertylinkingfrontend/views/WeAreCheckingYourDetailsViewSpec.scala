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
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavigationBarContent
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.WeAreCheckingYourDetailsView

class WeAreCheckingYourDetailsViewSpec extends ViewBaseSpec {
  lazy val view: WeAreCheckingYourDetailsView = inject[WeAreCheckingYourDetailsView]
  lazy val navBarContent: NavigationBarContent = createDefaultNavBar()

  val title = "We are checking your details"
  val p1 = "Your information has been submitted"
  val goBack = "Return to account home"

  object Selectors {
    val title = "#main-content > div > div.govuk-grid-column-two-thirds > form > h1"
    val p1 = "#main-content > div > div.govuk-grid-column-two-thirds > form > p:nth-child(2)"
    val goBack = "#main-content > div > div.govuk-grid-column-two-thirds > form > p:nth-child(3) > a"
  }

  "WeAreCheckingYourDetailsView" must {
    val weAreCheckingYourDetailsView = view(navBarContent)
    lazy implicit val document: Document = Jsoup.parse(weAreCheckingYourDetailsView.body)
    val htmlApply = view.apply(navBarContent).body
    val htmlRender = view.render(navBarContent, request, messages, mockConfig).body
    lazy val htmlF = view.f(navBarContent)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "should show correct title" in {
      elementText(Selectors.title) mustBe title
    }

    "should show correct p1" in {
      elementText(Selectors.p1) mustBe p1
    }

    "should show correct goBack" in {
      elementText(Selectors.goBack) mustBe goBack
    }
  }
}
