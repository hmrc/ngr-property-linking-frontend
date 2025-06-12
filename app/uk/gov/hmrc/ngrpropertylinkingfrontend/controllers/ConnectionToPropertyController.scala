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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.ConnectionToPropertyForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{FindAPropertyRepo, PropertyLinkingRepo}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.ConnectionToPropertyView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConnectionToPropertyController @Inject()(connectionToPropertyView: ConnectionToPropertyView,
                                               authenticate: AuthRetrievals,
                                               isRegisteredCheck: RegistrationAction,
                                               mcc: MessagesControllerComponents,
                                               findAPropertyRepo: FindAPropertyRepo,
                                               propertyLinkingRepo: PropertyLinkingRepo)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val ownerButton: NGRRadioButtons = NGRRadioButtons("Owner", Owner, Some(Hint(content = Text("Owns the property."))))
  private val occupierButton: NGRRadioButtons = NGRRadioButtons("Occupier",Occupier, Some(Hint(content = Text("Operates from the property."))))
  private val bothButton: NGRRadioButtons = NGRRadioButtons("Owner and occupier",OwnerAndOccupier, Some(Hint(content = Text("Owns and Operates from the property."))))
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("connection-to-property-radio"), Seq(ownerButton, occupierButton, bothButton))

  def show: Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(properties) =>
          val propertyAddress = properties.vmvProperty.addressFull
          Future.successful(Ok(connectionToPropertyView(
            form = form,
            radios = buildRadios(form, ngrRadio),
            navigationBarContent = createDefaultNavBar(),
            propertyAddress = propertyAddress,
            saveAndReturnHomeUrl = routes.ConnectionToPropertyController.saveAndReturnHome.url)))
        case None => Future.failed(throw new NotFoundException("Unable to find matching postcode"))
      }
    }
  }

  def submit: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      println(Console.RED + "=================================" + Console.RESET)
      form.bindFromRequest()
        .fold(
        formWithErrors => {
          val credId = request.credId.getOrElse("")
          propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
            case Some(properties) =>
              println(Console.RED + "*****************************************" + Console.RESET)
              val propertyAddress = properties.vmvProperty.addressFull
              Future.successful(BadRequest(connectionToPropertyView(
                form = formWithErrors,
                radios = buildRadios(formWithErrors, ngrRadio),
                navigationBarContent = createDefaultNavBar(),
                propertyAddress = propertyAddress,
                saveAndReturnHomeUrl = routes.ConnectionToPropertyController.saveAndReturnHome.url))
              )
            case None => Future.successful(Redirect(routes.NoResultsFoundController.show))
          }
        },
          propertySelectedForm => {
//            if(propertySelectedForm.radioValue.equals("Yes")) {
//              val credId = request.credId.getOrElse("")
//              for {
//                response <- propertyLinkingRepo.findByCredId(CredId(credId))
//                property = response
//                  .map(_.vmvProperty.addressFull)
//                  .getOrElse(throw new NotFoundException("No properties found on account"))
//                userAnswers = PropertyLinkingUserAnswers(CredId(credId), property)
//                _ <- propertyLinkingRepo.upsertProperty(userAnswers)
//              } yield Redirect(routes.CurrentRatepayerController.show)
//            } else {
              Future.successful(Redirect(routes.FindAPropertyController.show))
//            }
        }
      )
    }

  def saveAndReturnHome: Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      println(Console.RED + "_______________________________________" + Console.RESET)
      form.bindFromRequest()
        .fold(
          formWithErrors => {
            val credId = request.credId.getOrElse("")
            propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
              case Some(properties) =>
                println(Console.RED + "*****************************************" + Console.RESET)
                val propertyAddress = properties.vmvProperty.addressFull
                Future.successful(BadRequest(connectionToPropertyView(
                  form = formWithErrors,
                  radios = buildRadios(formWithErrors, ngrRadio),
                  navigationBarContent = createDefaultNavBar(),
                  propertyAddress = propertyAddress,
                  saveAndReturnHomeUrl = routes.ConnectionToPropertyController.saveAndReturnHome.url))
                )
              case None => Future.successful(Redirect(routes.NoResultsFoundController.show))
            }
          },
          propertySelectedForm => {
            //            if(propertySelectedForm.radioValue.equals("Yes")) {
            //              val credId = request.credId.getOrElse("")
            //              for {
            //                response <- propertyLinkingRepo.findByCredId(CredId(credId))
            //                property = response
            //                  .map(_.vmvProperty.addressFull)
            //                  .getOrElse(throw new NotFoundException("No properties found on account"))
            //                userAnswers = PropertyLinkingUserAnswers(CredId(credId), property)
            //                _ <- propertyLinkingRepo.upsertProperty(userAnswers)
            //              } yield Redirect(routes.CurrentRatepayerController.show)
            //            } else {
            Future.successful(Redirect(routes.FindAPropertyController.show))
            //            }
          }
        )
    }
}

