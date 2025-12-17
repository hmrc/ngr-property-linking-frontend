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
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAndPropertyLinkCheckAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit.ConnectionToPropertyAuditModel
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm.{Occupier, Owner, OwnerAndOccupier, form}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.AuditingService
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.Constants
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ConnectionToPropertyView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConnectionToPropertyController @Inject()(connectionToPropertyView: ConnectionToPropertyView,
                                               authenticate: AuthRetrievals,
                                               mandatoryCheck: RegistrationAndPropertyLinkCheckAction,
                                               mcc: MessagesControllerComponents,
                                               auditingService: AuditingService,
                                               propertyLinkingRepo: PropertyLinkingRepo)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val ownerButton: NGRRadioButtons = NGRRadioButtons(Constants.owner, Owner, Some("connectionToProperty.ownerHint"))
  private val occupierButton: NGRRadioButtons = NGRRadioButtons(Constants.occupier,Occupier, Some("connectionToProperty.occupierHint"))
  private val bothButton: NGRRadioButtons = NGRRadioButtons(Constants.ownerAndOccupier,OwnerAndOccupier, Some("connectionToProperty.ownerAndOccupierHint"))
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName(ConnectionToPropertyForm.formName), Seq(ownerButton, occupierButton, bothButton))

  def show: Action[AnyContent] = {
    (authenticate andThen mandatoryCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(properties) =>
          val propertyAddress = properties.vmvProperty.addressFull
          val preparedForm = properties.connectionToProperty match {
            case None        => form
            case Some(value) => form.fill(ConnectionToPropertyForm.stringToPropertyForm(value))
          }

          Future.successful(Ok(connectionToPropertyView(
            form = preparedForm,
            radios = buildRadios(preparedForm, ngrRadio),
            navigationBarContent = createDefaultNavBar(),
            propertyAddress = propertyAddress)))
        case None => Future.failed(throw new NotFoundException("Unable to find matching postcode"))
      }
    }
  }

  def submit: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      form.bindFromRequest().fold(
        formWithErrors => {
          val credId = request.credId.getOrElse("")
          propertyLinkingRepo.findByCredId(CredId(credId)).flatMap {
            case Some(properties) =>
              val propertyAddress = properties.vmvProperty.addressFull
              Future.successful(BadRequest(connectionToPropertyView(
                form = formWithErrors,
                radios = buildRadios(formWithErrors, ngrRadio),
                navigationBarContent = createDefaultNavBar(),
                propertyAddress = propertyAddress
              )))
            case None =>
              Future.successful(Redirect(routes.NoResultsFoundController.show))
          }
        },
        connectionToPropertyForm => {
          auditingService.extendedAudit(ConnectionToPropertyAuditModel(request.credId.getOrElse(""), connectionToPropertyForm, "check-your-answers"),
            uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes.ConnectionToPropertyController.show.url)

          val connectionToProperty = connectionToPropertyForm match {
            case ConnectionToPropertyForm.Owner => Constants.owner
            case ConnectionToPropertyForm.Occupier => Constants.occupier
            case ConnectionToPropertyForm.OwnerAndOccupier => Constants.ownerAndOccupier
          }

          propertyLinkingRepo.insertConnectionToProperty(
            credId = CredId(request.credId.getOrElse("")),
            connectionToProperty = connectionToProperty
          )
          Future.successful(Redirect(routes.CheckYourAnswersController.show))
        }
      )
    }
}

