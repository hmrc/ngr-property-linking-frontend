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

import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.Html
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.CurrentRatepayerForm.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.CurrentRatepayerForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.CurrentRatepayerView
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.DateTextFields
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CurrentRatepayerController @Inject()(currentRatepayerView: CurrentRatepayerView,
                                           dateTextFields: DateTextFields,
                                           authenticate: AuthRetrievals,
                                           isRegisteredCheck: RegistrationAction,
                                           propertyLinkingRepo: PropertyLinkingRepo,
                                           mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val beforeButton: NGRRadioButtons = NGRRadioButtons("Before 1 April 2026", Before)
  private def afterButton(form: Form[CurrentRatepayerForm])(implicit messages: Messages): NGRRadioButtons =
    NGRRadioButtons(radioContent = "On or after 1 April 2026", radioValue = After, conditionalHtml = Some(dateTextFields(form)))
  private def ngrRadio(form: Form[CurrentRatepayerForm])(implicit messages: Messages): NGRRadio =
    NGRRadio(NGRRadioName("current-ratepayer-radio"), Seq(beforeButton, afterButton(form)))
  
  def show(mode: String): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
        case Some(property) =>  Future.successful(Ok(currentRatepayerView(createDefaultNavBar, form, buildRadios(form, ngrRadio(form)), address = property.vmvProperty.addressFull, mode = mode)))
        case None => throw new NotFoundException("failed to find property from mongo")
      }

    }

  def submit(mode: String): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            //When validating from after apply, formError key is always empty. Below allows us to highlight the error field.
            val correctedFormErrors = formWithErrors.errors.map { formError =>
              (formError.key, formError.messages) match
              case ("", messages) if messages.contains("currentRatepayer.day.empty.error") || messages.contains("currentRatepayer.day.format.error") =>
                formError.copy(key = "day")
              case ("", messages) if messages.contains("currentRatepayer.month.empty.error") || messages.contains("currentRatepayer.month.format.error") =>
                formError.copy(key = "month")
              case ("", messages) if messages.contains("currentRatepayer.year.empty.error") || messages.contains("currentRatepayer.year.format.error")=>
                formError.copy(key = "year")
              case _ =>
                formError
            }
            val formWithCorrectedErrors = formWithErrors.copy(errors = correctedFormErrors)
            propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(throw new NotFoundException("failed to find credId from request")))).flatMap{
              case Some(property) =>  Future.successful(BadRequest(currentRatepayerView(createDefaultNavBar, formWithCorrectedErrors,
                buildRadios(formWithCorrectedErrors, ngrRadio(formWithCorrectedErrors)), address = property.vmvProperty.addressFull, mode = mode)))
              case None => throw new NotFoundException("failed to find property from mongo")
            },
          currentRatepayerForm =>
            def ratepayerDate: Option[LocalDate] =
              if (currentRatepayerForm.radioValue.equals("After"))
                Some(LocalDate.of(currentRatepayerForm.year.get.toInt, currentRatepayerForm.month.get.toInt, currentRatepayerForm.day.get.toInt))
              else
                None

            val credId = request.credId.getOrElse(throw new NotFoundException("failed to find credId from request"))
            propertyLinkingRepo.insertCurrentRatepayer(
              credId = CredId(credId),
              currentRatepayer = currentRatepayerForm.radioValue,
              becomeRatepayerDate = ratepayerDate
            )
            
            if(mode == "CYA")
              Future.successful(Redirect(routes.CheckYourAnswersController.show))
            else
              Future.successful(Redirect(routes.BusinessRatesBillController.show("")))
        )
    }
}
