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

import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.FindAPropertyConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSelect.buildSelect
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{NGRSelect, NGRSelectItem, NGRSelectName}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ManualPropertySearchForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.FindAPropertyRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ManualPropertySearchView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ManualSearchPropertyController @Inject()(manualPropertySearchView: ManualPropertySearchView,
                                               findAPropertyConnector: FindAPropertyConnector,
                                               authenticate: AuthRetrievals,
                                               isRegisteredCheck: RegistrationAction,
                                               mcc: MessagesControllerComponents,
                                               findAPropertyRepo: FindAPropertyRepo)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val anyItem: NGRSelectItem = NGRSelectItem("Any", "any")
  private val council1Item: NGRSelectItem = NGRSelectItem("Adur", "3085")
  private val council2Item: NGRSelectItem = NGRSelectItem("Somerset West & Taunton (West Somerset)", "3320")
  private val ngrSelect: NGRSelect = NGRSelect(NGRSelectName("council", "Local council"), Seq(anyItem, council1Item, council2Item), "govuk-select govuk-!-width-three-quarters")

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      Future.successful(Ok(manualPropertySearchView(form, createDefaultNavBar, buildSelect(form, ngrSelect))))
    }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => {
            val correctedFormErrors = formWithErrors.errors.map(formError =>
              if (formError.key.equals(""))
                formError.copy(key = "miniRateableValue")
              else
                formError
            )
            val formWithCorrectedErrors = formWithErrors.copy(errors = correctedFormErrors)
            Future.successful(BadRequest(manualPropertySearchView(formWithCorrectedErrors, createDefaultNavBar, buildSelect(formWithCorrectedErrors, ngrSelect))))
          },
          manualPropertySearch => {
//            val minValue: Long = manualPropertySearch.miniRateableValue.getOrElse(0)
//            val maxValue: Long = manualPropertySearch.maxRateableValue.getOrElse(0)
//            println(Console.YELLOW + + minValue + "++++++++++++++++++++++++++++++++++++++++" + maxValue + Console.RESET)
//            if (maxValue > 0 && minValue >= maxValue) {
//              println(Console.YELLOW + form + Console.RESET)
//              val data = (manualPropertySearch.productElementNames zip manualPropertySearch.productIterator.map {
//                case Some(v) => v.toString
//                case None => ""
//                case x => x.toString
//              }).toMap
//              val formWithErrors = form.copy(data= data, errors = Seq(FormError("miniRateableValue", Seq("manualSearchProperty.miniRateableValue.validation.error"))))
//              println(Console.YELLOW + formWithErrors + Console.RESET)
//              Future.successful(BadRequest(manualPropertySearchView(formWithErrors, createDefaultNavBar, buildSelect(formWithErrors, ngrSelect))))
//            } else
            Future.successful(Redirect(routes.NoResultsFoundController.show.url))
          }
        )
    }
}
