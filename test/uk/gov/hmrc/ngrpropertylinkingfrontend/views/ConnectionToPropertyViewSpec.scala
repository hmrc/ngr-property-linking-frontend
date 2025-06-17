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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm.{Occupier, Owner, OwnerAndOccupier}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ConnectionToPropertyView

class ConnectionToPropertyViewSpec extends ViewBaseSpec {
  lazy val view: ConnectionToPropertyView = inject[ConnectionToPropertyView]
  val address = "5 Brixham Marina, Berry Head Road, Brixham, Devon, TQ5 9BW"

  val title = "What is your connection to the property? - GOV.UK"
  val addressCaption = address
  val heading = "What is your connection to the property?"
  val radio1 = "Owner"
  val radio2 = "Occupier"
  val radio3 = "Owner and occupier"

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

  private val ownerButton: NGRRadioButtons = NGRRadioButtons("Owner", Owner, Some("Owns the property."))
  private val occupierButton: NGRRadioButtons = NGRRadioButtons("Occupier", Occupier, Some("Operates from the property."))
  private val bothButton: NGRRadioButtons = NGRRadioButtons("Owner and occupier", OwnerAndOccupier, Some("Owns and Operates from the property."))
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("connection-to-property-radio"), Seq(ownerButton, occupierButton, bothButton))
  val form = ConnectionToPropertyForm.form().fillAndValidate(ConnectionToPropertyForm.Owner)

  val radio: Radios = buildRadios(form, ngrRadio)

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div > form > div > div > h1"
    val addressCaption = "#main-content > div > div > form > div > div > span"
    val radio1 = "#main-content > div > div > form > div > div > div > div > div:nth-child(1) > label"
    val radio2 = "#main-content > div > div > form > div > div > div > div > div:nth-child(2) > label"
    val radio3 = "#main-content > div > div > form > div > div > div > div > div:nth-child(3) > label"
  }

  "ConnectionToPropertyView" must {
    val connectionToPropertyView = view(form, radio, content, address)
    lazy implicit val document: Document = Jsoup.parse(connectionToPropertyView.body)
    val htmlApply = view.apply(form, radio, content, address).body
    val htmlRender = view.render(form, radio, content, address, request, messages, mockConfig).body
    lazy val htmlF = view.f(form, radio, content, address)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must nit be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "show the correct title" in {
      elementText(Selectors.navTitle) mustBe title
    }

    "show the correct heading" in {
      elementText(Selectors.heading) mustBe heading
    }

    "show the correct address caption" in {
      elementText(Selectors.addressCaption) mustBe addressCaption
    }

    "show the correct radio button for owner" in {
      elementText(Selectors.radio1) mustBe radio1
    }

    "show the correct radio button for occupier" in {
      elementText(Selectors.radio2) mustBe radio2
    }

    "show the correct radio button for owner and occupier" in {
      elementText(Selectors.radio3) mustBe radio3
    }

  }
}

