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
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView

class UploadBusinessRatesBillViewSpec extends ViewBaseSpec {

  lazy val view: UploadBusinessRatesBillView = inject[UploadBusinessRatesBillView]
  val title = "Upload your business rates bill - GOV.UK"
  val heading = "Upload your business rates bill"
  val p1 = "1 Mulholland Drive, LA, 6BH 4PE"
  val p2 = "The file must be a Word document, PDF or image (PNG or JPG) and be less than 25MB."
  val continueButton = "Continue"

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
    val heading = "h1.govuk-heading-l"
    val caption = "h2.govuk-caption-l"
    val paragraph = "p.govuk-caption-m"
    val fileInputLabel = "label.govuk-label"
    val fileInput = "input#file-upload-1"
    val continueButton = "button.govuk-button"
  }

  "UploadBusinessRatesBillView" must {
    "render consistently using apply and render" must {
      val rendered = view.apply(content, "/search-again", "/dashboard")(request, messages, mockConfig)
      val renderedHtml = view.render(content, "/search-again", "/dashboard", request, messages, mockConfig).body
      lazy val htmlF = view.f(content, "/search-again", "/dashboard")

      "htmlF is not empty" in {
        htmlF.toString() must not be empty
      }

      "apply must be the same as render" in {
        rendered.body mustBe renderedHtml
      }

      "render is not empty" in {
        renderedHtml must not be empty
      }
    }

    "display the correct static content" must {
      implicit val document: Document =
        Jsoup.parse(view(content, "/search-again", "/dashboard").body)

      "have the correct page title" in {
        elementText(Selectors.navTitle) mustBe title
      }

      "have the correct main heading" in {
        elementText(Selectors.heading) mustBe heading
      }

      "have the correct caption text" in {
        elementText(Selectors.caption) must include(p1)
      }

      "have the correct paragraph below heading" in {
        elementText(Selectors.paragraph) must include(p2)
      }

      "have a file input label" in {
        elementText(Selectors.fileInputLabel) mustBe "Upload a file"
      }

      "have a file input field" in {
        document.select(Selectors.fileInput).attr("type") mustBe "file"
      }

      "have a continue button with correct text" in {
        elementText(Selectors.continueButton) mustBe continueButton
      }
    }
  }
}
