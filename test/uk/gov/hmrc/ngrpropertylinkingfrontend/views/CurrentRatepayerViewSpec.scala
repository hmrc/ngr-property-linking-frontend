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
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.CurrentRatepayerForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.CurrentRatepayerView

class CurrentRatepayerViewSpec extends ViewBaseSpec {
  val view: CurrentRatepayerView = inject[CurrentRatepayerView]
  val address = "5 Brixham Marina, Berry Head Road, Brixham, Devon, TQ5 9BW"

  val title = "When did you become the current ratepayer? - GOV.UK"
  val addressCaption = address
  val heading = "When did you become the current ratepayer?"
  val p1 = "This tells us when your responsibilities started under business rates reform. The earliest this can be is 1 April 2026."
  val detailsSummary = "How to tell if you are the current rate payer"
  val contentP1 = "You are the current ratepayer if you get a business rates bill and (either of these apply):"
  val contentL1 = "you pay business rates for all or part of the property"
  val contentL2 = "you do not pay business rates because you get relief such as small business rates relief"
  val contentP2 = "You are not the current ratepayer if someone else pays the business rates. For example, you own the property and a tenant pays the business rates."
  val radio1 = "Before 1 April 2026"
  val radio2 = "On or after 1 April 2026"
  val dayInputLabel = "Day"
  val monthInputLabel = "Month"
  val yearInputLabel = "Year"

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

  private val beforeButton: NGRRadioButtons = NGRRadioButtons("Before", Before)
  private val afterButton: NGRRadioButtons = NGRRadioButtons("After", After)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-address-radio"), Seq(beforeButton, afterButton))
  val form = CurrentRatepayerForm.form.fillAndValidate(CurrentRatepayerForm("Before", None))
  val radio: Radios = buildRadios(form, ngrRadio)
  val mode: String = ""

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div.govuk-grid-column-two-thirds > form > h1"
    val addressCaption = "#main-content > div > div.govuk-grid-column-two-thirds > form > span"
    val p1 = "#main-content > div > div.govuk-grid-column-two-thirds > form > p"
    val detailsSummary = "#how-to-tell-if-you-are-the-current-rate-payer > summary > span"
    val contentP1 = "#how-to-tell-if-you-are-the-current-rate-payer > div > p:nth-child(1)"
    val contentL1 = "#how-to-tell-if-you-are-the-current-rate-payer > div > li:nth-child(2)"
    val contentL2 = "#how-to-tell-if-you-are-the-current-rate-payer > div > li:nth-child(3)"
    val contentP2 = "#how-to-tell-if-you-are-the-current-rate-payer > div > p:nth-child(5)"
    val radio1 = "#main-content > div > div > form > div > div > div > div > div > div > div:nth-child(1) > label"
    val radio2 = "#main-content > div > div > form > div > div > div > div > div > div > div:nth-child(2) > label"
  }

  "CurrentRatepayerView" must {

    val currentRatepayerView = view(content, form, radio, address, mode)
    lazy implicit val document: Document = Jsoup.parse(currentRatepayerView.body)
    val htmlApply = view.apply(content, form , radio, address, mode).body
    val htmlRender = view.render(content, form , radio , address, mode, request, messages, mockConfig).body
    lazy val htmlF = view.f(content, form , radio , address, mode)

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

    "show correct address caption" in {
      elementText(Selectors.addressCaption) mustBe addressCaption
    }

    "show correct p1" in {
      elementText(Selectors.p1) mustBe p1
    }

    "show correct details summary" in {
      elementText(Selectors.detailsSummary) mustBe detailsSummary
    }

    "show correct details content p1" in {
      elementText(Selectors.contentP1) mustBe contentP1
    }

    "show correct details content l1" in {
      elementText(Selectors.contentL1) mustBe contentL1
    }

    "show correct details content l2" in {
      elementText(Selectors.contentL2) mustBe contentL2
    }

    "show correct details content p2" in {
      elementText(Selectors.contentP2) mustBe contentP2
    }
  }
}
