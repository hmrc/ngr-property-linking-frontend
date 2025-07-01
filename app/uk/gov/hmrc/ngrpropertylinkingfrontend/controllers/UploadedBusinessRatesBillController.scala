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
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow, UpscanRecord}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadedBusinessRatesBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{PropertyLinkingRepo, UpscanRepo}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadedBusinessRatesBillController @Inject()(uploadedView: UploadedBusinessRatesBillView,
                                                    upscanRepo: UpscanRepo,
                                                    authenticate: AuthRetrievals,
                                                    isRegisteredCheck: RegistrationAction,
                                                    propertyLinkingRepo: PropertyLinkingRepo,
                                                    mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private def createSummaryList(fileName: String, status: String, downloadUrl: Option[String])(implicit messages: Messages): SummaryList = {
    SummaryList(
      Seq(
        NGRSummaryListRow(
          fileName,
          None,
          Seq( status match{
            case status if status.equals("READY") => messages("uploadedBusinessRatesBill.uploaded")
            case status if status.equals("REJECTED") => messages("uploadedBusinessRatesBill.uploaded")
            case status if status.equals("QUARANTINE") => messages("uploadedBusinessRatesBill.uploaded-virus")
            case status if status.equals("UNKNOWN") => messages("uploadedBusinessRatesBill.uploaded-unknown")
          }),
          Some(Link(Call("GET", routes.UploadBusinessRatesBillController.show(None).url), "remove-link", "Remove")),
          Some(Link(Call("GET", downloadUrl.getOrElse("")), "file-download-link", "")),
          if (status.equals("READY")) Some("govuk-tag govuk-tag--green") else Some("govuk-tag govuk-tag--red"),
          "govuk-summary-list__key_width"
        )
      ).map(summarise)
    )
  }

  def show: Action[AnyContent] = (authenticate andThen isRegisteredCheck).async { implicit request =>
    request.credId match {
      case Some(rawCredId) =>
        val credId = CredId(rawCredId)

        //TODO address this Thread.sleep. Short term it may be possible to reduce the wait time, long term replace it
        // Keeping delay (NOTE: this blocks a thread — avoid in production)
        Thread.sleep(500)

        upscanRepo.findByCredId(credId).flatMap {
          case Some(record) =>
            val fileName = record.fileName.getOrElse(throw new NotFoundException("Missing Name"))
            propertyLinkingRepo.findByCredId(credId).map {
              case Some(property) =>
                record.failureReason match {
                  case Some(errorToDisplay) =>
                    Redirect(routes.UploadBusinessRatesBillController.show(Some(errorToDisplay)))
                  case None =>
                    Ok(uploadedView(
                      createDefaultNavBar,
                      createSummaryList(fileName, record.status, record.downloadUrl),
                      property.vmvProperty.addressFull,
                      false
                    ))
                }
              case None =>
                throw new NotFoundException("failed to find property from mongo")
            }
          case None =>
            throw new RuntimeException(s"No UpscanRecord found for credId: ${credId.value}")
        }
      case None =>
        Future.failed(new RuntimeException("No credId found in request"))
    }
  }

  def submit: Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId: CredId = CredId(request.credId.getOrElse(throw new NotFoundException("Not found credId on account")))
      upscanRepo.findByCredId(credId).flatMap { upScan =>
        val fileName: String = upScan.flatMap(_.fileName).getOrElse(throw new Exception("Not found file name"))
        propertyLinkingRepo.insertEvidenceDocument(credId = credId, evidenceDocument = fileName).flatMap {
          case Some(answers) =>
            if (answers.connectionToProperty.isDefined) {
              Future.successful(Redirect(routes.CheckYourAnswersController.show.url))
            } else {
              Future.successful(Redirect(routes.ConnectionToPropertyController.show.url))
            }
          case None => throw new NotFoundException("No responses found")
        }
      }
    }
  }
}
