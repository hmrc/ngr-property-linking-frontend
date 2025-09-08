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
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.RemoveBusinessRatesBillView

class RemoveBusinessRatesBillViewSpec extends ViewBaseSpec {
  private val view: RemoveBusinessRatesBillView = inject[RemoveBusinessRatesBillView]
  private val address = "1 Mulholland Drive, LA, 6BH 4PE"
  private val fileName = "testFile.png"
  private val testFileUrl = "downloadYourFile.org"
  private val uploadId = UploadId.generate()

  private val content: NavigationBarContent = NavBarPageContents.CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(),
    notifications = Some(1)
  )

  private val testSummaryList: SummaryList = SummaryList(
    Seq(
      NGRSummaryListRow(
        titleMessageKey = fileName,
        captionKey = None,
        value = Seq(messages("uploadedBusinessRatesBill.uploaded")),
        changeLink = None,
        titleLink = Some(Link(Call("GET", testFileUrl), "file-download-link", "")),
        valueClasses = Some("govuk-tag govuk-tag--green")
      )
    ).map(summarise)
  )

  private object Selectors {
    val title = "head > title"
    val address = "#main-content > div > div.govuk-grid-column-two-thirds > form > span"
    val mainText = "#main-content > div > div.govuk-grid-column-two-thirds > form > h1"
    val minorText = "#main-content > div > div.govuk-grid-column-two-thirds > form > p"
    val fileName = "#main-content > div > div.govuk-grid-column-two-thirds > form > dl > div > dt > a"
    val uploaded = "#main-content > div > div.govuk-grid-column-two-thirds > form > dl > div > dd"
    val removeButton = "#continue"
    val cancelLink = "#main-content > div > div.govuk-grid-column-two-thirds > form > div > p > a"
  }

  "RemoveBusinessRatesBillView" must {
    "render consistently using apply and render" must {
      val rendered = view.apply(content, address, testSummaryList, uploadId)(request, messages, mockConfig)
      val renderedHtml = view.render(content, address, testSummaryList, uploadId, request, messages, mockConfig).body
      lazy val htmlF = view.f(content, address, testSummaryList, uploadId)

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

    "display the correct content" must {
      implicit val document: Document =
        Jsoup.parse(view(content, address, testSummaryList, uploadId).body)

      "have the correct page title" in {
        elementText(Selectors.title) mustBe "Manage Your Business Rates Valuation - GOV.UK"
      }

      "have the correct address caption text" in {
        elementText(Selectors.address) mustBe address
      }

      "have the correct main text" in {
        elementText(Selectors.mainText) mustBe "Are you sure you want to remove this file?"
      }

      "have the correct minor text" in {
        elementText(Selectors.minorText) mustBe "If you remove this file, you need to provide another one."
      }

      "have the correct file name displayed" in {
        elementText(Selectors.fileName) mustBe fileName
      }

      "file name has the correct href" in {
        element(Selectors.fileName).attribute("href").toString must include(testFileUrl)
      }

      "have the Uploaded label displayed" in {
        elementText(Selectors.uploaded) mustBe "Uploaded"
      }

      "have a remove button with correct text" in {
        elementText(Selectors.removeButton) mustBe "Remove file"
      }

      "have a cancel link with correct text" in {
        elementText(Selectors.cancelLink) mustBe "Cancel"
      }

      "cancel link has the correct href" in {
        element(Selectors.cancelLink).attribute("href").toString must include("/ngr-property-linking-frontend/uploaded-business-rates-bill")
      }
    }
  }
}
