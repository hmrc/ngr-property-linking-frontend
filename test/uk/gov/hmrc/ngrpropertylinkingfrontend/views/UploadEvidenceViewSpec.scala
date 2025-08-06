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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadEvidenceForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadEvidenceView

class UploadEvidenceViewSpec extends ViewBaseSpec {
  val view: UploadEvidenceView = inject[UploadEvidenceView]
  val address = "5 Brixham Marina, Berry Head Road, Brixham, Devon, TQ5 9BW"

  val title = "What evidence can you provide? - GOV.UK"
  val addressCaption = address
  val heading = "What evidence can you provide?"
  val p1 = "The date of the evidence must overlap with you paying business rates for the property."
  val p2 = "If you can't provide any of this evidence, you won't be able to add your property."
  val radio1 = "Lease"
  val radio2 = "Land Registry title"
  val radio3 = "Licence to occupy"
  val radio4 = "Service charges statement"
  val radio5 = "Stamp Duty Land Tax form"
  val radio6 = "Utility bill"
  val radio7 = "Water rate demand"

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

  private val leaseButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.Lease", Lease)
  private val landRegistryButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.LandRegistry", LandRegistry)
  private val licenceButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.Licence", Licence)
  private val serviceStatementButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.ServiceStatement", ServiceStatement)
  private val stampDutyButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.StampDuty", StampDuty)
  private val utilityBillButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.UtilityBill", UtilityBill)
  private val waterRateButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.WaterRate", WaterRate)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("upload-evidence-radio"), Seq(leaseButton, landRegistryButton,
    licenceButton, serviceStatementButton, stampDutyButton, utilityBillButton, waterRateButton))
  val form = UploadEvidenceForm.form.fillAndValidate(UploadEvidenceForm("LandRegistry"))
  val radio: Radios = buildRadios(form, ngrRadio)

  object Selectors {
    val navTitle = "head > title"
    val heading = "#main-content > div > div.govuk-grid-column-two-thirds > form > h1"
    val addressCaption = "#main-content > div > div.govuk-grid-column-two-thirds > form > span"
    val p1 = "#main-content > div > div.govuk-grid-column-two-thirds > form > p"
    val p2 = "#main-content > div > div.govuk-grid-column-two-thirds > form > p.govuk-body"
    val radio1 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(1) > label"
    val radio2 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(2) > label"
    val radio3 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(3) > label"
    val radio4 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(4) > label"
    val radio5 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(5) > label"
    val radio6 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(6) > label"
    val radio7 = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > div > div:nth-child(7) > label"
  }

  "CurrentRatepayerView" must {
    val uploadEvidenceView = view(content, form, radio, address)
    lazy implicit val document: Document = Jsoup.parse(uploadEvidenceView.body)
    val htmlApply = view.apply(content, form , radio, address).body
    val htmlRender = view.render(content, form , radio , address, request, messages, mockConfig).body
    lazy val htmlF = view.f(content, form , radio , address)

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

    "show correct p2" in {
      elementText(Selectors.p2) mustBe p2
    }

    "show correct radio 1 label" in {
      elementText(Selectors.radio1) mustBe radio1
    }

    "show correct radio 2 label" in {
      elementText(Selectors.radio2) mustBe radio2
    }

    "show correct radio 3 label" in {
      elementText(Selectors.radio3) mustBe radio3
    }

    "show correct radio 4 label" in {
      elementText(Selectors.radio4) mustBe radio4
    }

    "show correct radio 5 label" in {
      elementText(Selectors.radio5) mustBe radio5
    }

    "show correct radio 6 label" in {
      elementText(Selectors.radio6) mustBe radio6
    }

    "show correct radio 7 label" in {
      elementText(Selectors.radio7) mustBe radio7
    }
  }
}
