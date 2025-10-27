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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ManualPropertySearchForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ManualPropertySearchView

class ManualPropertySearchViewSpec extends ViewBaseSpec {
  lazy val view: ManualPropertySearchView = inject[ManualPropertySearchView]
  lazy val title = "What is the address? - GOV.UK"
  lazy val heading = "What is the address?"
  lazy val backLink = "Back"
  lazy val line1Label = "Address line 1 (optional)"
  lazy val line2Label = "Address line 2 (optional)"
  lazy val townLabel = "Town or city (optional)"
  lazy val countyLabel = "County (optional)"
  lazy val postcodeLabel = "Postcode"
  lazy val continueButton = "Find address"
  lazy val line1EmptyErrorMessage = "Error: Enter address line 1, typically the building and street"
  lazy val line1OverMaxLengthErrorMessage = "Error: Address line 1 must be 100 characters or less"
  lazy val line2OverMaxLengthErrorMessage = "Error: Address line 2 must be 100 characters or less"
  lazy val townEmptyErrorMessage = "Error: Enter town or city"
  lazy val townOverMaxLengthErrorMessage = "Error: Town or city must be 100 characters or less"
  lazy val countyOverMaxLengthErrorMessage = "Error: County must be 100 characters or less"
  lazy val postcodeEmptyErrorMessage = "Error: Enter postcode"
  lazy val invalidErrorMessage = "Error: Enter a full UK postcode"
  lazy val propertyNameOver100ErrorMessage = "Error: Property name or number must be 100 characters or less"
  lazy val miniRateableValueGreaterThanMaxRateableValueErrorMessage = "Minimum rateable value must be lower than maximum rateable value"
  lazy val miniRateableValueOverMaxErrorMessage = "Error: Minimum rateable value must be £2,147,483,647 or lower"
  lazy val maxRateableValueOverMaxErrorMessage = "Error: Maximum rateable value must be £2,147,483,647 or lower"
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
    val lin1Label = "#main-content > div > div > form > div > div > div:nth-child(2) > label"
    val lin2Label = "#main-content > div > div > form > div > div > div:nth-child(3) > label"
    val townLabel = "#main-content > div > div > form > div > div > div:nth-child(4) > label"
    val countyLabel = "#main-content > div > div > form > div > div > div:nth-child(5) > label"
    val postcodeLabel = "#main-content > div > div > form > div > div > div:nth-child(6) > label"
    val continueButton = "#continue"
    val line1ErrorMessage = "#addressLine1-error"
    val line2ErrorMessage = "#addressLine2-error"
    val townErrorMessage = "#town-error"
    val countyErrorMessage = "#county-error"
    val postcodeErrorMessage = "#postcode-error"
    val miniRateableValueErrorMessage = "#miniRateableValue-error"
    val maxRateableValueErrorMessage = "#maxRateableValue-error"
  }

  "ManualPropertySearchView" must {
    "produce the same output for apply() and render()" must {
      val form = ManualPropertySearchForm
        .form
        .fillAndValidate(ManualPropertySearchForm(Some("Address Line 1"), None, Some("town"), None, Some(Postcode("TQ5 9BW")), None, None, None, None, None, None))
      val manualPropertySearchView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(manualPropertySearchView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, false, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content, false)

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

      "show correct address line1 label" in {
        elementText(Selectors.lin1Label) mustBe line1Label
      }

      "show correct address line2 label" in {
        elementText(Selectors.lin2Label) mustBe line2Label
      }

      "show correct town label" in {
        elementText(Selectors.townLabel) mustBe townLabel
      }

      "show correct county label" in {
        elementText(Selectors.countyLabel) mustBe countyLabel
      }

      "show correct postcode label" in {
        elementText(Selectors.postcodeLabel) mustBe postcodeLabel
      }

      "show correct continue button" in {
        elementText(Selectors.continueButton) mustBe continueButton
      }
    }

    "produce the same output for apply() and render() when max error messages on line1, line2, town and county" in {
      val form = ManualPropertySearchForm
        .form
        .fillAndValidate(ManualPropertySearchForm(Some(over100Characters), Some(over100Characters), Some(over100Characters), Some(over100Characters), Some(Postcode("TQ5 9BW")), None, None, None, None, None, None))
      val manualPropertySearchView = view(form, content)
      lazy implicit val document: Document = Jsoup.parse(manualPropertySearchView.body)
      val htmlApply = view.apply(form, content).body
      val htmlRender = view.render(form, content, false, request, messages, mockConfig).body
      lazy val htmlF = view.f(form, content, false)
      htmlF.toString() must not be empty
      htmlApply mustBe htmlRender
      elementText(Selectors.line1ErrorMessage) mustBe line1OverMaxLengthErrorMessage
      elementText(Selectors.line2ErrorMessage) mustBe line2OverMaxLengthErrorMessage
      elementText(Selectors.townErrorMessage) mustBe townOverMaxLengthErrorMessage
      elementText(Selectors.countyErrorMessage) mustBe countyOverMaxLengthErrorMessage
    }
  }
}
