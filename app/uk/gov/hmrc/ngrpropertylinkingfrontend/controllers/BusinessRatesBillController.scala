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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.BusinessRatesBillForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.BusinessRatesBillForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.BusinessRatesBillView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BusinessRatesBillController @Inject()(businessRatesBillView: BusinessRatesBillView,
                                            authenticate: AuthRetrievals,
                                            isRegisteredCheck: RegistrationAction,
                                            propertyLinkingRepo: PropertyLinkingRepo,
                                            mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes", Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No", No)
  private val ngrRadio: NGRRadio = NGRRadio(
    NGRRadioName("business-rates-bill-radio"),
    Seq(yesButton, noButton),
    hint = Some("uploadBusinessRatesBill.hint"))

  def show: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(property) =>
          val preparedForm = property.businessRatesBill match {
            case None => form
            case Some(value) => form.fill(BusinessRatesBillForm(value))
          }
          Future.successful(Ok(businessRatesBillView(
            createDefaultNavBar, preparedForm, buildRadios(preparedForm, ngrRadio), address = property.vmvProperty.addressFull)))
        case None => throw new NotFoundException("failed to find property from mongo")
      }

    }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
            case Some(property) => Future.successful(BadRequest(businessRatesBillView(
              createDefaultNavBar,
              formWithErrors,
              buildRadios(formWithErrors, ngrRadio),
              address = property.vmvProperty.addressFull)))
            case None => throw new NotFoundException("failed to find property from mongo")
          },
          businessRatesBillForm =>
            propertyLinkingRepo.insertBusinessRatesBill(
              credId = CredId(request.credId.getOrElse("")),
              businessRatesBill = businessRatesBillForm.radioValue
            )
            if (businessRatesBillForm.radioValue == "Yes") {
              propertyLinkingRepo.insertUploadEvidence(CredId(request.credId.getOrElse("")), null)
              Future.successful(Redirect(routes.UploadBusinessRatesBillController.show(None, None)))
            } else {
              Future.successful(Redirect(routes.UploadEvidenceController.show))
            }
        )
    }
}

