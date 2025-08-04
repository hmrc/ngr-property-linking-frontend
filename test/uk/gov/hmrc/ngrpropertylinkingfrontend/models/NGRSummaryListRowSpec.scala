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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.i18n.{Lang, Messages}
import play.api.mvc.Call
import play.api.test.Helpers.stubMessagesApi
import uk.gov.hmrc.govukfrontend.views.html.components.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow as RowData

class NGRSummaryListRowSpec extends AnyWordSpec with Matchers {

  implicit val messages: Messages = stubMessagesApi().preferred(Seq(Lang("en")))

  "NGRSummaryListRow.summarise" should {

    "render a row with caption and non-empty values" in {
      val row = RowData(
        titleMessageKey = "test.title",
        captionKey = Some("test.caption"),
        value = Seq("value1", "value2"),
        changeLink = None
      )

      val result = NGRSummaryListRow.summarise(row)

      result.key.content shouldBe a[HtmlContent]
      result.value.content shouldBe a[HtmlContent]
      result.actions shouldBe None
    }

    "render a row without a caption" in {
      val row = RowData(
        titleMessageKey = "test.title",
        captionKey = None,
        value = Seq("value1"),
        changeLink = None
      )

      val result = NGRSummaryListRow.summarise(row)

      result.key.content shouldBe Text(messages("test.title"))
      result.value.content shouldBe a[HtmlContent]
    }

    "render a row with a change link" in {
      val row = RowData(
        titleMessageKey = "test.title",
        captionKey = None,
        value = Seq("value1"),
        changeLink = Some(Link(
          href = Call(method = "Post", url = "http://example.com"),
          messageKey = "change",
          visuallyHiddenMessageKey = Some("hidden-text"),
          linkId = "change-id"
        ))
      )

      val result = NGRSummaryListRow.summarise(row)

      result.actions shouldBe defined
      result.actions.get.items.head.href shouldBe "http://example.com"
      result.actions.get.items.head.attributes("id") shouldBe "change-id"
    }

    "render a row with empty values and only a link" in {
      val row = RowData(
        titleMessageKey = "test.title",
        captionKey = None,
        value = Seq.empty,
        changeLink = Some(Link(
          href = Call(method = "Post", url = "http://example.com"),
          messageKey = "change",
          visuallyHiddenMessageKey = Some("hidden-text"),
          linkId = "link-id"
        ))
      )

      val result = NGRSummaryListRow.summarise(row)

      result.value.content shouldBe a[HtmlContent]
      val html = result.value.content.asInstanceOf[HtmlContent].value.body
      html should include("""<a id="link-id"""")
    }

  }
}

