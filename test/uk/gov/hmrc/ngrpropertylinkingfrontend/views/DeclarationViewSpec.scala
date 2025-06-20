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
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.DeclarationView

class DeclarationViewSpec extends ViewBaseSpec {
  lazy val view: DeclarationView = inject[DeclarationView]
  lazy val navBarContent: NavigationBarContent = createDefaultNavBar()

  object Selectors {
    val title = "#main-content > div > div > form > h1"
    val p1 = "#main-content > div > div > form > p"
    val button = "#continue"
  }

  "DeclarationView" must {
    val declarationView = view(navBarContent)
    lazy implicit val document: Document = Jsoup.parse(declarationView.body)
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

    "should show correct content" in {
      elementText(Selectors.title) mustBe "Declaration"
      elementText(Selectors.p1) mustBe "By submitting this request to add a property, you declare that to the best of your knowledge, the information given is correct and complete."
      elementText(Selectors.button) mustBe "Accept and send"
    }
  }
}
