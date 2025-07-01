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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.BusinessRatesBillForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.BusinessRatesBillView

class BusinessRatesBillViewSpec extends ViewBaseSpec {
  lazy val view: BusinessRatesBillView = inject[BusinessRatesBillView]
  val address = "5 Brixham Marina, Berry Head Road, Brixham, Devon, TQ5 9BW"
  val title = "Do you have a business rates bill for the property? - GOV.UK"
  val addressCaption = address
  val heading = "Do you have a business rates bill for the property?"
  val hint = "You need the most recent bill you have for the property."
  val radio1 = "Yes"
  val radio2 = "No"

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

  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("business-rates-bill-radio"), Seq(yesButton, noButton))
  val form = BusinessRatesBillForm.form.fillAndValidate(BusinessRatesBillForm("Yes"))
  val radio: Radios = buildRadios(form, ngrRadio)

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div.govuk-grid-column-two-thirds > form > h1"
    val addressCaption = "#main-content > div > div.govuk-grid-column-two-thirds > form > span"
    val hint = "#business-rates-bill-radio-hint"
    val radio1 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div.govuk-radios.govuk-radios > div:nth-child(1) > label"
    val radio2 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div.govuk-radios.govuk-radios > div:nth-child(2) > label"
  }

  "CurrentRatepayerView" must {

    val currentRatepayerView = view(content, form, radio, address, "")
    lazy implicit val document: Document = Jsoup.parse(currentRatepayerView.body)
    val htmlApply = view.apply(content, form , radio, address, "").body
    val htmlRender = view.render(content, form , radio , address, "", request, messages, mockConfig).body
    lazy val htmlF = view.f(content, form , radio , address, "")

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

    "show yes radio" in {
      elementText(Selectors.radio1) mustBe radio1
    }

    "show no radio" in {
      elementText(Selectors.radio2) mustBe radio2
    }
  }
}
