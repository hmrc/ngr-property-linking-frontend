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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.NGRConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject()(checkYourAnswersView: CheckYourAnswersView,
                                           authenticate: AuthRetrievals,
                                           isRegisteredCheck: RegistrationAction,
                                           propertyLinkingRepo: PropertyLinkingRepo,
                                           ngrConnector: NGRConnector,
                                           mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private def createSummaryRows(userAnswers: PropertyLinkingUserAnswers)(implicit messages: Messages): Seq[SummaryListRow] = {
    val rows: Seq[NGRSummaryListRow] = Seq(
      NGRSummaryListRow(
        messages("checkYourAnswers.property.title"),
        None,
        Seq(userAnswers.vmvProperty.addressFull),
        changeLink = Some(Link(href = routes.FindAPropertyController.show, linkId = "property-address", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-address")))
      ),
      NGRSummaryListRow(
        messages("checkYourAnswers.propertyReference.title"),
        None,
        Seq(userAnswers.vmvProperty.localAuthorityReference),
        None
      ),
      NGRSummaryListRow(
        messages("checkYourAnswers.currentRatepayer.title"),
        None,
        Seq(userAnswers.currentRatepayer.map(currentRatepayer =>
            if (currentRatepayer.isBeforeApril) "checkYourAnswers.currentRatepayer.before" else "checkYourAnswers.currentRatepayer.after")
          .getOrElse(throw new NotFoundException("Could not find current ratepayer"))),
        changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "current-ratepayer", messageKey = "service.change", visuallyHiddenMessageKey = Some("current-ratepayer")))
      ), //TODO CHANGE CURRENT RATEPAYER
      NGRSummaryListRow(
        messages("checkYourAnswers.businessRatesBill"),
        None,
        Seq(userAnswers.userHasBusinessRatesBill.getOrElse(throw new NotFoundException("Could not find business rates bill"))),
        changeLink = Some(Link(href = routes.BusinessRatesBillController.show, linkId = "business-rates-bill", messageKey = "service.change", visuallyHiddenMessageKey = Some("business-rates-bill")))
      )
    ) //TODO CHANGE CURRENT RATEPAYER

    val rows2: Seq[NGRSummaryListRow] = Seq(
      NGRSummaryListRow(
        messages("checkYourAnswers.EvidenceDocument"),
        None,
        Seq(userAnswers.evidenceDocumentName.getOrElse(throw new NotFoundException("evidence document not found"))),
        changeLink = Some(Link(href = routes.BusinessRatesBillController.show, linkId = "business-rates-bill", messageKey = "service.change", visuallyHiddenMessageKey = Some("business-rates-bill")))
      ),
      NGRSummaryListRow(
        messages("checkYourAnswers.PropertyConnection"),
        None,
        Seq(userAnswers.connectionToProperty.getOrElse("")),
        changeLink = Some(Link(href = routes.ConnectionToPropertyController.show, linkId = "property-connection", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-connection")))
      )
    )

    def uploadEvidenceRow(uploadEvidence: String): NGRSummaryListRow = NGRSummaryListRow(
      messages("checkYourAnswers.uploadEvidence"),
      None,
      Seq(messages(s"uploadEvidence.$uploadEvidence")),
      changeLink = Some(Link(href = routes.BusinessRatesBillController.show, linkId = "upload-evidence", messageKey = "service.change", visuallyHiddenMessageKey = Some("upload-evidence")))
    )

    val summaryListRows: Seq[NGRSummaryListRow] =
      userAnswers.uploadEvidence match
        case Some(uploadEvidence) => (rows :+ uploadEvidenceRow(uploadEvidence)) ++ rows2
        case None => rows ++ rows2

    summaryListRows.map(summarise)
  }

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(userAnswers) => Future.successful(Ok(
          checkYourAnswersView(navigationBarContent = createDefaultNavBar, summaryList = SummaryList(createSummaryRows(userAnswers = userAnswers)))))
        case None => throw new NotFoundException("failed to find property from mongo")
      }
    }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val credId = CredId(request.credId.getOrElse(""))
      for {
        maybeAnswers <- propertyLinkingRepo.findByCredId(credId)
        userAnswers <- maybeAnswers match {
          case Some(result) => Future.successful(result)
          case None => Future.failed(new NotFoundException("Failed to find the user answers"))
        }
        response <- ngrConnector.upsertPropertyLinkingUserAnswers(userAnswers)
        result <- if (response.status == CREATED) {
          Future.successful(Redirect(routes.DeclarationController.show))
        } else {
          Future.failed(new Exception("Failed upsert to backend"))
        }
      } yield result
    }
}
