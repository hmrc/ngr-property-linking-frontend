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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
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
    Seq(
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
        Seq(userAnswers.currentRatepayer.map(answer =>
          if(answer == "Before"){"checkYourAnswers.currentRatepayer.before"}else{"checkYourAnswers.currentRatepayer.after"})
          .getOrElse(throw new NotFoundException("Could not find current ratepayer"))),
        changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "current-ratepayer", messageKey = "service.change", visuallyHiddenMessageKey = Some("current-ratepayer")))
      ), //TODO CHANGE CURRENT RATEPAYER
      NGRSummaryListRow(
        messages("checkYourAnswers.businessRatesBill"),
        None,
        Seq(userAnswers.credId.value.toString),
        changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "business-rates-bill", messageKey = "service.change", visuallyHiddenMessageKey = Some("business-rates-bill")))
      ), //TODO CHANGE CURRENT RATEPAYER
      NGRSummaryListRow(
        messages("checkYourAnswers.EvidenceDocument"),
        None,
        Seq(userAnswers.credId.value.toString),
        changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "evidence-document", messageKey = "service.change", visuallyHiddenMessageKey = Some("evidence-document")))
      ), //TODO CHANGE CURRENT RATEPAYER
      NGRSummaryListRow(
        messages("checkYourAnswers.PropertyConnection"),
        None,
        Seq(userAnswers.credId.value.toString),
        changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "property-connection", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-connection")))
      ) //TODO CHANGE CURRENT RATEPAYER
    ).map(summarise)
  }

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
        case Some(userAnswers) =>  Future.successful(Ok(
          checkYourAnswersView(navigationBarContent = createDefaultNavBar,  summaryList = SummaryList(createSummaryRows(userAnswers = userAnswers)))))
        case None => throw new NotFoundException("failed to find property from mongo")
      }
    }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      for {
        response <- ngrConnector.upsertPropertyLinkingUserAnswers(
            propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap( result => result.)
        )
        result <- if (response.status == CREATED) {
          Future.successful(Redirect(routes.WhatYouNeedController.show)
        } else {
          Future.failed(new Exception("Failed upsert to backend"))
        }
      } yield result
    }
}
