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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.paginate

import uk.gov.hmrc.govukfrontend.views.Aliases.{Content, Table}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}

sealed trait TableRowData {
  def html: Content
  val classes: Option[String]
}
final case class TableRowText(value: String, classes: Option[String] = None) extends TableRowData {
  override def html: Text = Text(value)
}
final case class TableRowLink(value: String, label: String, classes: Option[String] = None) extends TableRowData {
  override def html: HtmlContent = {
    HtmlContent(s"""<a href="$value" class="govuk-link">$label</a>""")
  }
}

final case class TableHeader(header: String, classes: String, colspan: Option[Int] = None) {
  def htmlContent(): HeadCell = {
    HeadCell(content = Text(header), classes = classes, colspan = colspan)
  }
}

final case class TableData(headers: Seq[TableHeader], rows: Seq[Seq[TableRowData]], caption: Option[String] = None, captionClasses: String = "govuk-table__caption--m") {
  def toTable: Table = {
    Table(
      head = Some(headers.map(header => header.htmlContent())),
      rows = rows.map(
        row => row.map(
          cell => TableRow(
            content = cell.html,
            classes = cell.classes.getOrElse("")
          )
        )
      ),
      caption = caption,
      captionClasses = captionClasses,
      firstCellIsHeader = false,
      classes = "govuk-!-width-full"
    )
  }
}