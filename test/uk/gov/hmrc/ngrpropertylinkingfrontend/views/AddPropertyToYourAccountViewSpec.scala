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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyToYourAccountView

class AddPropertyToYourAccountViewSpec extends ViewBaseSpec {
  lazy val view: AddPropertyToYourAccountView = inject[AddPropertyToYourAccountView]
  val title = "Add a property to your account - GOV.UK"
  val heading = "Add a property to your account"
  val p1 = "From 1 April 2026, you must tell us within 60 days if you become the ratepayer of a property. You can do this by adding the property to your account."
  val p2 = "If you were the ratepayer before 1 April 2026, you need to add your property so you can report changes to your property, rent or lease."
  val p3Inset = "You must be the current ratepayer for any property you want to add."
  val p4 = "You are the current ratepayer if you get a business rates bill and (either of these apply):"
  val p5Bullet1 = "you pay business rates for all or part of the property"
  val p5Bullet2 = "you do not pay business rates because you get relief such as small business rates relief"
  val p6 = "You are not the current ratepayer if someone else pays the business rates. For example, you own the property and a tenant pays the business rates."

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
    val heading = "#main-content > div > div > form > div > div > h1"
    val p1 = "#main-content > div > div > form > div > div > p:nth-child(2)"
    val p2 = "#main-content > div > div > form > div > div > p:nth-child(3)"
    val p3Inset = "#main-content > div > div > form > div > div > div"
    val p4 = "#main-content > div > div > form > div > div > p:nth-child(5)"
    val p5Bullet1 = "#main-content > div > div > form > div > div > ul > li:nth-child(1)"
    val p5Bullet2 = "#main-content > div > div > form > div > div > ul > li:nth-child(2)"
    val p6 = "#main-content > div > div > form > div > div > p:nth-child(7)"
  }

  "AddPropertyToYourAccountView" must {
    val addPropertyToYourAccountView = view(content)
    lazy implicit val document: Document = Jsoup.parse(addPropertyToYourAccountView.body)
    val htmlApply = view.apply(content).body
    val htmlRender = view.render(content, request, messages, mockConfig).body
    lazy val htmlF = view.f(content)

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

    "show correct p3Insert" in {
      elementText(Selectors.p3Inset) mustBe p3Inset
    }

    "show correct p4" in {
      elementText(Selectors.p4) mustBe p4
    }

    "show correct p5Bullet1" in {
      elementText(Selectors.p5Bullet1) mustBe p5Bullet1
    }

    "show correct p5Bullet2" in {
      elementText(Selectors.p5Bullet2) mustBe p5Bullet2
    }

    "show correct p6" in {
      elementText(Selectors.p6) mustBe p6
    }
  }
}
