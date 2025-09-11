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
import uk.gov.hmrc.govukfrontend.views.Aliases
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.StringContextOps
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadStatus.{Failed, InProgress, UploadedSuccessfully}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRateBillView

class UploadedBusinessRateBillViewSpec extends ViewBaseSpec {
  
  val view: UploadedBusinessRateBillView = inject[UploadedBusinessRateBillView]
  val title = "Upload your business rates bill - GOV.UK"
  val heading = "Upload your business rates bill"
  val serviceStatementTitle = "Upload your Service charges statement - GOV.UK"
  val serviceStatementHeading = "Upload your Service charges statement"
  val address = "address"
  val fileName = "test.png"
  val uploadedTag = "Uploaded"
  val remove = "Remove"
  val uploading = "Uploading"
  val failed = "Failed"
  val p1 = "The file must be a PDF or image (PNG or JPG) and be less than 25MB."

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
    val caption = "#main-content > div > div > span"
    val fileName = "#main-content > div > div > dl > div > dt > a"
    val removeLink = "#remove-link"
    val uploadText = "#main-content > div > div > dl > div > dt"
    val failedText = "#main-content > div > div > dl > div > dt"
    val paragraph = "#main-content > div > div.govuk-grid-column-two-thirds > p.govuk-caption-m.hmrc-caption-m"
  }
  
  val uploadSuccessful: Seq[Aliases.SummaryListRow] = Seq(
    NGRSummaryListRow(
      "test.png",
      None,
      Seq(messages("uploadedBusinessRatesBill.uploaded")),
      Some(Link(Call("GET", "dummyLink"), "remove-link", "Remove")),
      Some(Link(Call("GET", "http://example.com/dummyLink"), "file-download-link", "")),
      Some("govuk-tag govuk-tag--green")
    )
  ).map(summarise)
  
  val uploadFailed: Seq[Aliases.SummaryListRow] = Seq(
      NGRSummaryListRow(
        "Failed",
        None,
        Seq(""),
        None,
        None,
        None
      )
    ).map(summarise)

  val uploadInProgress: Seq[Aliases.SummaryListRow] = Seq(
    NGRSummaryListRow(
      "Uploading",
      None,
      Seq(""),
      None,
      None,
      None
    )
  ).map(summarise)
  
  "UploadedBusinessRatesBillView" when {
    "render consistenting using apply and render" should {

      val rendered = view.apply(navigationBarContent = content, summaryList = SummaryList(uploadSuccessful), addressFull = "address", uploadId = UploadId("1234"), status = UploadedSuccessfully("test.png", ".png", url"http://example.com/dummyLink", Some(120L)), None)(request, messages, mockConfig)
      val renderedHtml = view.render(navigationBarContent = content, summaryList = SummaryList(uploadSuccessful), addressFull = "address", uploadId = UploadId("1234"), status = UploadedSuccessfully("test.png", ".png", url"http://example.com/dummyLink", Some(120L)), None, request, messages, mockConfig).body
      lazy val htmlF = view.f(content, SummaryList(uploadSuccessful), "address", UploadId("1234"), UploadedSuccessfully("test.png", ".png", url"https://example.com/dummyLink", Some(120L)), None)

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

    "Display the correct static content if the upload is successful" should {

      val rendered = view.apply(navigationBarContent = content, summaryList = SummaryList(uploadSuccessful), addressFull = "address", uploadId = UploadId("1234"), status = UploadedSuccessfully("test.png", ".png", url"http://example.com/dummyLink", Some(120L)), None)(request, messages, mockConfig)
      val renderedHtml = view.render(navigationBarContent = content, summaryList = SummaryList(uploadSuccessful), addressFull = "address", uploadId = UploadId("1234"), status = UploadedSuccessfully("test.png", ".png", url"http://example.com/dummyLink", Some(120L)), None, request, messages, mockConfig).body
      lazy val htmlF = view.f(content, SummaryList(uploadSuccessful), "address", UploadId("1234"), UploadedSuccessfully("test.png", ".png", url"https://example.com/dummyLink", Some(120L)), None)

      implicit val document: Document =
        Jsoup.parse(view(
          navigationBarContent = content,
          summaryList = SummaryList(uploadSuccessful),
          addressFull = "address",
          uploadId = UploadId("12345"),
          status = UploadedSuccessfully("test.png", ".png", url"http://example.com/dummyLink", Some(120L)),
          evidenceType = None
        ).body)


      "have the correct page title" in {
        elementText(Selectors.navTitle) mustBe title
      }

      "have the correct main heading" in {
        elementText(Selectors.heading) mustBe heading
      }

      "have the correct caption text" in {
        elementText(Selectors.caption) mustBe address
      }

      "have the correct file name" in {
        elementText(Selectors.fileName) mustBe fileName
      }

      "have a remove link" in {
        elementText(Selectors.removeLink) mustBe remove
      }

      "have the correct paragraph below heading" in {
        elementText(Selectors.paragraph) must include(p1)
      }
    }
    
    //TODO these tests will need to be updated when we know the design of the inprogress and failed states

    "Display the correct static content if the upload is inProgress" should {

      implicit val document: Document =
        Jsoup.parse(view(
          navigationBarContent = content,
          summaryList = SummaryList(uploadInProgress),
          addressFull = "address",
          uploadId = UploadId("12345"),
          status = InProgress,
          evidenceType = None
        ).body)


      "have the correct page title" in {
        elementText(Selectors.navTitle) mustBe title
      }

      "have the correct main heading" in {
        elementText(Selectors.heading) mustBe heading
      }

      "have the correct caption text" in {
        elementText(Selectors.caption) mustBe address
      }
      
      "have the correct uploading test" in {
        elementText(Selectors.uploadText) mustBe uploading
      }

      "have the correct paragraph below heading" in {
        elementText(Selectors.paragraph) must include(p1)
      }
      
    }

    "Display the correct static content if the upload is failed" should {

      implicit val document: Document =
        Jsoup.parse(view(
          navigationBarContent = content,
          summaryList = SummaryList(uploadFailed),
          addressFull = "address",
          uploadId = UploadId("12345"),
          status = Failed,
          evidenceType = None
        ).body)


      "have the correct page title" in {
        elementText(Selectors.navTitle) mustBe title
      }

      "have the correct main heading" in {
        elementText(Selectors.heading) mustBe heading
      }

      "have the correct caption text" in {
        elementText(Selectors.caption) mustBe address
      }

      "have the correct uploading test" in {
        elementText(Selectors.failedText) mustBe failed
      }

      "have the correct paragraph below heading" in {
        elementText(Selectors.paragraph) must include(p1)
      }

    }
  }
}
