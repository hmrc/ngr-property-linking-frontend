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
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadStatus.UploadedSuccessfully
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.{UploadId, UploadStatus}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.UploadProgressTracker
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRateBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UploadedBusinessRatesBillController @Inject()(uploadProgressTracker: UploadProgressTracker,
                                                    uploadedBusinessRateBillView: UploadedBusinessRateBillView,
                                                    authenticate: AuthRetrievals,
                                                    isRegisteredCheck: RegistrationAction,
                                                    propertyLinkingRepo: PropertyLinkingRepo,
                                                    mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = (authenticate andThen isRegisteredCheck).async { implicit request =>
    val credId = CredId(request.credId.getOrElse(throw new NotFoundException("CredId not found in UploadedBusinessRatesBillController.show()")))
    for {
      maybePropertyLinkingUserAnswers <- propertyLinkingRepo.findByCredId(credId)
      propertyLinkingUserAnswers = maybePropertyLinkingUserAnswers.getOrElse(throw new NotFoundException("Property not found in UploadedBusinessRatesBillController.show()"))
      uploadId = UploadId(propertyLinkingUserAnswers.evidenceDocumentUploadId.getOrElse(throw new NotFoundException("evidenceDocumentUploadId not found in UploadedBusinessRatesBillController.show()")))
      address = propertyLinkingUserAnswers.vmvProperty.addressFull
      uploadEvidence = propertyLinkingUserAnswers.uploadEvidence
      uploadResult <- uploadProgressTracker.getUploadResult(uploadId)
    }
    yield {
      uploadResult match
      case Some(UploadStatus.UploadedSuccessfully(evidenceDocument, mimeType, downloadUrl, size)) =>
        val downloadUrlString: String = downloadUrl.toString
        propertyLinkingRepo.insertEvidenceDocument(credId, evidenceDocument, downloadUrlString, uploadId.value)
        Ok(uploadedBusinessRateBillView(
          createDefaultNavBar,
          buildSuccessSummaryList(evidenceDocument, downloadUrlString),
          address,
          uploadId,
          UploadStatus.UploadedSuccessfully(evidenceDocument, mimeType, downloadUrl, size),
          uploadEvidence))
      case Some(UploadStatus.InProgress) =>
        Ok(uploadedBusinessRateBillView(
          createDefaultNavBar,
          buildInProgressOrFailedSummaryList("Uploading"),
          address,
          uploadId,
          UploadStatus.InProgress,
          uploadEvidence))
      case Some(UploadStatus.Failed) =>
        Ok(uploadedBusinessRateBillView(
          createDefaultNavBar,
          buildInProgressOrFailedSummaryList("Failed"),
          address,
          uploadId,
          UploadStatus.Failed,
          uploadEvidence))
      case None => BadRequest(s"Upload with id ${uploadId.value} not found")}
  }

  def buildSuccessSummaryList(evidenceDocument: String, downloadUrl: String)(implicit messages: Messages): SummaryList = {
    SummaryList(
      Seq(
        NGRSummaryListRow(
          titleMessageKey = evidenceDocument,
          captionKey = None,
          value = Seq(messages("uploadedBusinessRatesBill.uploaded")),
          changeLink = Some(Link(Call("GET", routes.RemoveBusinessRatesBillController.show.url), "remove-link", "Remove")),
          titleLink = Some(Link(Call("GET", downloadUrl), "file-download-link", "")),
          valueClasses = Some("govuk-tag govuk-tag--green")
        )
      ).map(summarise)
    )
  }

  def buildInProgressOrFailedSummaryList(uploadStatusString: String)(implicit messages: Messages): SummaryList = {
    SummaryList(
      Seq(
        NGRSummaryListRow(
          uploadStatusString,
          None,
          Seq(""),
          None,
          None,
          None
        )
      ).map(summarise)
    )
  }
}
