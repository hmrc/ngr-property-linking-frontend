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
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.properties.VMVProperty
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyRequestSentView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddPropertyRequestSentController @Inject()(view: AddPropertyRequestSentView,
                                                 authenticate: AuthRetrievals,
                                                 isRegisteredCheck: RegistrationAction,
                                                 mcc: MessagesControllerComponents,
                                                 propertyLinkingRepo: PropertyLinkingRepo)(implicit appConfig: AppConfig, executionContext: ExecutionContext)  extends FrontendController(mcc) with I18nSupport {

  private def createSummaryRows(property: VMVProperty)(implicit messages: Messages): Seq[SummaryListRow] = {
    Seq(
      NGRSummaryListRow(messages("Address"), None, Seq(property.addressFull), None),
      NGRSummaryListRow(messages("Property Reference"), None, Seq(property.localAuthorityReference), None)
    ).map(summarise)
  }

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      val email: String = request.email.getOrElse(throw new NotFoundException("email not found on account"))
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(answers) =>
          val property = answers.vmvProperty
          val ref = answers.requestSentReference.getOrElse("")
          val summaryRows = createSummaryRows(property)
          Future.successful(Ok(view(ref, SummaryList(summaryRows), createDefaultNavBar,email)))
        case None => throw new NotFoundException("failed to find property from mongo")
      }

    }

}

