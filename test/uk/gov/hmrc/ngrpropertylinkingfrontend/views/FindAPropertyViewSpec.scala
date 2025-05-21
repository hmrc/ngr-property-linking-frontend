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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.Postcode
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.FindAProperty
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.FindAPropertyView

class FindAPropertyViewSpec extends ViewBaseSpec {
  lazy val view: FindAPropertyView = inject[FindAPropertyView]
  lazy val title = "Find a property - GOV.UK"
  lazy val heading = "Find a property"
  lazy val backLink = "Back"
  lazy val postcodeLabel = "Postcode"
  lazy val propertyNameLabel = "Property name or number (optional)"
  lazy val continueButton = "Find address"
  lazy val hint = "For example, 116, The Mill, or Suite 10"
  lazy val emptyErrorMessage = "Error: Enter postcode"
  lazy val invalidErrorMessage = "Error: Enter a full UK postcode"
  lazy val propertyNameOver100ErrorMessage = "Error: Property name or number must be 100 characters or less"
  lazy val over100Characters = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"

  val content: NavigationBarContent = NavBarPageContents.CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(),
    notifications = Some(1)
  )

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > form > div > div > h1"
    val backLink = "#main-content > div > div > div > a"
    val postcodeLabel = "#main-content > div > div > form > div > div > div:nth-child(2) > label"
    val propertyNameLabel = "#main-content > div > div > form > div > div > div:nth-child(4) > label"
    val hint = "#property-name-value-hint"
    val continueButton = "#continue"
    val errorMessage = "#postcode-value-error"
    val propertyNameErrorMessage = "#property-name-value-error"
  }

  "FindAPropertyView" must {
    "produce the same output for apply() and render()" must {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode("TQ5 9BW"), None))
      val findAPropertyView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(findAPropertyView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)

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

      "show correct back link" in {
        elementText(Selectors.backLink) mustBe backLink
      }

      "show correct postcode label" in {
        elementText(Selectors.postcodeLabel) mustBe postcodeLabel
      }

      "show correct property name label" in {
        elementText(Selectors.propertyNameLabel) mustBe propertyNameLabel
      }

      "show correct hint" in {
        elementText(Selectors.hint) mustBe hint
      }

      "show correct continue button" in {
        elementText(Selectors.continueButton) mustBe continueButton
      }
    }
    "produce the same output for apply() and render() when valid postcode without space in between" in {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode("TQ59BW"), None))
      val findAPropertyView = view(form, content)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
    }

    "produce the same output for apply() and render() with valid property name" in {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode("TQ5 9BW"), Some("5")))
      val findAPropertyView = view(form, content)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
    }

    "show missing postcode error correctly" in {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode(""), None))
      val findAPropertyView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(findAPropertyView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
      elementText(Selectors.errorMessage) mustBe emptyErrorMessage
    }

    "show invalid postcode error correctly" in {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode("AAA9 9AA"), None))
      val findAPropertyView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(findAPropertyView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
      elementText(Selectors.errorMessage) mustBe invalidErrorMessage
    }

    "show property name exceed max length error correctly" in {
      val form = FindAProperty
        .form()
        .fillAndValidate(FindAProperty(Postcode("AA9 9AA"), Some(over100Characters)))
      val findAPropertyView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(findAPropertyView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
      elementText(Selectors.propertyNameErrorMessage) mustBe propertyNameOver100ErrorMessage
    }
  }
}
