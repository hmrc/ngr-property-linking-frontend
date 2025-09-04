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

package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers

import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.{NotFoundException, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.RemoveBusinessRatesBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.PropertyLinkingUserAnswers

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.net.{URI, URL}

@Singleton
class RemoveBusinessRatesBillController @Inject()(removeView: RemoveBusinessRatesBillView,
                                                  authenticate: AuthRetrievals,
                                                  isRegisteredCheck: RegistrationAction,
                                                  propertyLinkingRepo: PropertyLinkingRepo,
                                                  mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId: CredId = CredId(request.credId.getOrElse(throw new NotFoundException("CredId not found in RemoveBusinessRatesBillController.show()")))

      propertyLinkingRepo.findByCredId(credId).map {
        case Some(PropertyLinkingUserAnswers(credId, vmvProperty, _, _, _, _, Some(evidenceDocument), Some(evidenceDocumentUrl), _, _)) =>
          val summaryList: SummaryList = buildSummaryList(evidenceDocument, URI(evidenceDocumentUrl).toURL)
          Ok(removeView(createDefaultNavBar, vmvProperty.addressFull, summaryList))
        case Some(_) => throw new NotFoundException("Fields not found in RemoveBusinessRatesBillController.show()")
        case None => throw new NotFoundException("Property not found in RemoveBusinessRatesBillController.show()")
      }
    }
  }

  def remove: Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId: CredId = CredId(request.credId.getOrElse(throw new NotFoundException("CredId not found in RemoveBusinessRatesBillController.remove()")))

      propertyLinkingRepo.findByCredId(credId).flatMap {
        case Some(propertyLinkingUserAnswers) if propertyLinkingUserAnswers.evidenceDocument.isDefined && propertyLinkingUserAnswers.evidenceDocumentUploadId.isDefined =>
          propertyLinkingRepo.deleteEvidenceDocument(propertyLinkingUserAnswers.credId).map { deleted =>
            if (deleted) {
              Redirect(routes.UploadBusinessRatesBillController.show(None, propertyLinkingUserAnswers.uploadEvidence))
            } else {
              throw new RuntimeException("Failed to delete evidence document in RemoveBusinessRatesBillController.remove()")
            }
          }
        case Some(_) => throw new NotFoundException("EvidenceDocumentUploadId not found in RemoveBusinessRatesBillController.remove()")
        case None => throw new NotFoundException("Property not found in RemoveBusinessRatesBillController.remove()")
      }
    }
  }

  def buildSummaryList(fileName: String, downloadUrl: URL)(implicit messages: Messages): SummaryList = {
    SummaryList(
      rows = Seq(
        NGRSummaryListRow(
          titleMessageKey = fileName,
          captionKey = None,
          value = Seq(messages("uploadedBusinessRatesBill.uploaded")),
          changeLink = None,
          titleLink = Some(Link(Call("GET", downloadUrl.toString), "file-download-link", "")),
          valueClasses = Some("govuk-tag govuk-tag--green")
        )
      ).map(summarise),
      classes = "govuk-summary-list--long-key"
    )
  }
}
