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
import uk.gov.hmrc.govukfrontend.views.Aliases
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{NGRRadio, NGRRadioButtons, NGRRadioName, NGRSummaryListRow, No, PropertyLinkingUserAnswers, Yes}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.PropertySelectedView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.PropertySelectedForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.{VMVProperty, LookUpVMVProperties}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{FindAPropertyRepo, PropertyLinkingRepo}
import java.text.NumberFormat
import java.util.Locale

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PropertySelectedController @Inject()(propertySelectedView: PropertySelectedView,
                                           authenticate: AuthRetrievals,
                                           isRegisteredCheck: RegistrationAction,
                                           mcc: MessagesControllerComponents,
                                           findAPropertyRepo: FindAPropertyRepo,
                                           propertyLinkingRepo: PropertyLinkingRepo)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  private val yesButton: NGRRadioButtons = NGRRadioButtons("Yes",Yes)
  private val noButton: NGRRadioButtons = NGRRadioButtons("No",No)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("confirm-property-radio"), Seq(yesButton, noButton))

  def formatRateableValue(rateableValue: Long): String = {
    val ukFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    ukFormatter.format(rateableValue).replaceAll("[.][0-9]{2}", "")
  }
  
  private def createSummaryRows(property: VMVProperty)(implicit messages: Messages): Seq[SummaryListRow] = {
    Seq(
      NGRSummaryListRow(messages("Address"), None, Seq(property.addressFull), None),
      NGRSummaryListRow(messages("Property Reference"), None, Seq(property.localAuthorityReference), None),
      NGRSummaryListRow(messages("Local Council"), None, Seq("Torbay"), None),
      NGRSummaryListRow(messages("Description"), None, Seq(property.valuations.map(_.descriptionText).last), None),
      NGRSummaryListRow(messages("Rateable value"), None, Seq(formatRateableValue(property.valuations.last.rateableValue)), None),
    ).map(summarise)
  }

  def show(index: Int): Action[AnyContent] = {
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      findAPropertyRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
        case Some(properties) =>
          val selectedProperty = properties.vmvProperties.properties(index)
          Future.successful(Ok(propertySelectedView(
            form = form,
            radios = buildRadios(form, ngrRadio),
            summaryList = SummaryList(createSummaryRows(property = selectedProperty)),
            navigationBarContent = createDefaultNavBar(), index = index, dashboardUrl = appConfig.ngrDashboardUrl)))
        case None => Future.failed(throw new NotFoundException("Unable to find matching postcode"))
      }
    }
  }

  def submit(index: Int): Action[AnyContent] =
    (authenticate andThen isRegisteredCheck).async { implicit request =>
      form.bindFromRequest()
        .fold(
        formWithErrors => {
          val credId = request.credId.getOrElse("")
          findAPropertyRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap{
            case Some(properties) =>
              val selectedProperty = properties.vmvProperties.properties(index)
              Future.successful(BadRequest(
                propertySelectedView(
                  formWithErrors,
                  buildRadios(formWithErrors, ngrRadio),
                  SummaryList(createSummaryRows(selectedProperty)),
                  createDefaultNavBar(),
                  index,
                  appConfig.ngrDashboardUrl
                )
              ))
            case None => Future.successful(Redirect(routes.NoResultsFoundController.show))
          }
        },
          propertySelectedForm => {
            if(propertySelectedForm.radioValue.equals("Yes")) {
              val credId = request.credId.getOrElse("")
              for {
                response <- findAPropertyRepo.findByCredId(CredId(credId))
                property = response
                  .map(_.vmvProperties.properties(index))
                  .getOrElse(throw new NotFoundException("No properties found on account"))
                userAnswers = PropertyLinkingUserAnswers(CredId(credId), property)
                _ <- propertyLinkingRepo.upsertProperty(userAnswers)
              } yield Redirect(routes.CurrentRatepayerController.show(""))
            } else {
              Future.successful(Redirect(routes.SingleSearchResultController.show(1)))
            }
        }
      )
    }
}

