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
import play.api.data.*
import play.api.data.Forms.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{UploadForm, UpscanFileReference, UpscanInitiateResponse}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView

class UploadBusinessRatesBillViewSpec extends ViewBaseSpec {

  lazy val view: UploadBusinessRatesBillView = inject[UploadBusinessRatesBillView]
  val title = "Upload your business rates bill - GOV.UK"
  val heading = "Upload your business rates bill"
  val p1 = "1 Mulholland Drive, LA, 6BH 4PE"
  val p2 = "The file must be a Word document, PDF or image (PNG) and be less than 25MB."
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
    val paragraph = "#main-content > div > div.govuk-grid-column-two-thirds > div > p"
    val fileInput = "input#file-upload-1"
    val continueButton = "button.govuk-button"
  }

  val form: Form[String] = Form(single("text" -> nonEmptyText))
  val upscanForm: UploadForm = UploadForm("", Map("key" -> "value"))
  val upscanResponse: UpscanInitiateResponse = UpscanInitiateResponse(UpscanFileReference("ref"), "foo", Map("test" -> "test"))


  "UploadBusinessRatesBillView" must {
    "render consistently using apply and render" must {

      val rendered = view.apply(
        form = form,
        upscanResponse = upscanResponse,
        attributes = Map("accept" -> ".pdf,.png,.docx",
          "data-max-file-size" -> "100000000",
          "data-min-file-size" -> "1000"),
        errorMessage = None,
        address = "address",
        navigationBarContent = content,
        searchAgainUrl = "searchAgain",
        dashboardUrl = "dashboard")(request, messages, mockConfig)

      val renderedHtml = view.render(
        form = form,
        upscanResponse = upscanResponse,
        attributes = Map("accept" -> ".pdf,.png,.docx",
          "data-max-file-size" -> "100000000",
          "data-min-file-size" -> "1000"),
        errorMessage = None,
        address = "address",
        navigationBarContent = content,
        searchAgainUrl = "searchAgain",
        dashboardUrl = "dashboard", request, messages, mockConfig).body

      lazy val htmlF = view.f(form, upscanResponse, Map("accept" -> ".pdf,.png,.docx",
        "data-max-file-size" -> "100000000",
        "data-min-file-size" -> "1000"), None, "address", content, "searchAgain", "dashboard")


      "apply must be the same as render" in {
        rendered.body mustBe renderedHtml
      }

      "htmlF is not empty" in {
        htmlF.toString() must not be empty
      }

      "render is not empty" in {
        renderedHtml must not be empty
      }
    }

        "display the correct static content" must {
          implicit val document: Document =
            Jsoup.parse(view(
              form,
              upscanResponse,
              attributes = Map("accept" -> ".pdf,.png,.docx",
              "data-max-file-size" -> "100000000",
              "data-min-file-size" -> "1000"),
              None,
              "address",
              content,
              "searchAgain",
              "dashboard").body)

          "have the correct page title" in {
            elementText(Selectors.navTitle) mustBe title
          }

          "have the correct main heading" in {
            elementText(Selectors.heading) mustBe heading
          }

          "have the correct caption text" in {
            elementText(Selectors.caption) must include("address")
          }

          "have the correct paragraph below heading" in {
            elementText(Selectors.paragraph) must include(p2)
          }

          "have a continue button with correct text" in {
            elementText(Selectors.continueButton) mustBe continueButton
          }
        }
      }
  }