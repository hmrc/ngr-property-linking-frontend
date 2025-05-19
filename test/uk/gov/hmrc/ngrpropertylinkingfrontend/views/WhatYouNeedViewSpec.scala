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
import play.api.i18n.Messages
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.WhatYouNeedView

class WhatYouNeedViewSpec extends ViewBaseSpec {
  lazy val view: WhatYouNeedView = inject[WhatYouNeedView]

  object Strings {
    val title = "What you need"
    val p1 = "You need your most recent business rates bill to prove you are the current ratepayer. Your council will send you a business rates bill even if you get relief such as small business rates relief."
    val p2 = "If you have lost or cannot find your bill, contact your local council (opens in new tab)."
    val p3 = "If you became the ratepayer on or after the 1 April 2026, you need to give us the exact date you became the ratepayer."
    val p4 = "We can usually give you a decision on your request to add a property within 15 working days."
    val continue = "Continue"
  }

  object Selectors {
    val title = "#main-content > div > div > form > h1"
    val p1 = "#main-content > div > div > form > p:nth-child(2)"
    val p2 = "#main-content > div > div > form > p:nth-child(3)"
    val p3 = "#main-content > div > div > form > p:nth-child(4)"
    val p4 = "#main-content > div > div > form > p:nth-child(5)"
    val continue = "#continue"
  }

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

  private def createLink(implicit messages: Messages): String = {
    val text = messages("whatYouNeed.p2")
    val linkText = messages("whatYouNeed.link")
    val link = s"""<a href="https://www.gov.uk/contact-your-local-council-about-business-rates" target="_blank">$linkText</a>"""
    val content = text.replace(linkText, link)
    s"""<p class="govuk-body">$content</p>"""
  }

  "AddPropertyToYourAccountView" must {
    val addPropertyToYourAccountView = view(content, createLink)
    lazy implicit val document: Document = Jsoup.parse(addPropertyToYourAccountView.body)
    val htmlApply = view.apply(content, createLink).body
    val htmlRender = view.render(content, createLink, request, messages, mockAppConfig).body
    lazy val htmlF = view.f(content, createLink)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "show correct text" in {
      elementText(Selectors.title) mustBe Strings.title
      elementText(Selectors.p1) mustBe Strings.p1
      elementText(Selectors.p2) mustBe Strings.p2
      elementText(Selectors.p3) mustBe Strings.p3
      elementText(Selectors.p4) mustBe Strings.p4
      elementText(Selectors.continue) mustBe Strings.continue
    }

  }



}
