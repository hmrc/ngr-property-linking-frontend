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

  def storeAndShowUploadProgress(credId: CredId, uploadStatus: UploadStatus, evidence: Option[String])(implicit messages: Messages): SummaryList = {
    uploadStatus match
      case UploadStatus.UploadedSuccessfully(name, mimeType, downloadUrl, size) => {
        propertyLinkingRepo.insertEvidenceDocument(credId, name)
        SummaryList(
          Seq(
            NGRSummaryListRow(
              name,
              None,
              Seq(messages("uploadedBusinessRatesBill.uploaded")),
              Some(Link(Call("GET", routes.UploadBusinessRatesBillController.show(None, evidence).url), "remove-link", "Remove")),
              Some(Link(Call("GET", downloadUrl.toString), "file-download-link", "")),
              Some("govuk-tag govuk-tag--green")
            )
          ).map(summarise)
        )
      }
      case UploadStatus.InProgress => SummaryList(
        Seq(
          NGRSummaryListRow(
            "Uploading",
            None,
            Seq(""),
            None,
            None,
            None
          )
        ).map(summarise)
      )
      case UploadStatus.Failed => SummaryList(Seq(
        NGRSummaryListRow(
          "Failed",
          None,
          Seq(""),
          None,
          None,
          None
        )
      ).map(summarise)
        
      )
  }

  def show(uploadId: UploadId, evidence: Option[String]): Action[AnyContent] = (authenticate andThen isRegisteredCheck).async { implicit request =>
    val credId = CredId(request.credId.getOrElse(throw new NotFoundException("Not found cred id")))
    for
      maybeProperty <- propertyLinkingRepo.findByCredId(credId)
      uploadResult <- uploadProgressTracker.getUploadResult(uploadId)
    yield uploadResult match
      case Some(result) => Ok(uploadedBusinessRateBillView(
        createDefaultNavBar,
        storeAndShowUploadProgress(credId, result, evidence),
        maybeProperty.map(_.vmvProperty.addressFull).getOrElse(throw new NotFoundException("Not found property on account")),
        uploadId,
        result,
        evidence))
      case None => BadRequest(s"Upload with id $uploadId not found")
  }
}
