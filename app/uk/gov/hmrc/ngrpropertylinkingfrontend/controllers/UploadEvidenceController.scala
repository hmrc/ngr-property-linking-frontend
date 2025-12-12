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
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.ngrpropertylinkingfrontend.actions.{AuthRetrievals, RegistrationAction, RegistrationAndPropertyLinkCheckAction}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRRadio.buildRadios
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit.UploadEvidenceAuditModel
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadEvidenceForm
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadEvidenceForm.form
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo
import uk.gov.hmrc.ngrpropertylinkingfrontend.services.AuditingService
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.CurrencyHelper
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadEvidenceView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UploadEvidenceController @Inject()(uploadEvidenceView: UploadEvidenceView,
                                         authenticate: AuthRetrievals,
                                         mandatoryCheck: RegistrationAndPropertyLinkCheckAction,
                                         auditingService: AuditingService,
                                         propertyLinkingRepo: PropertyLinkingRepo,
                                         mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with CurrencyHelper {

  private val leaseButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.Lease", Lease)
  private val landRegistryButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.LandRegistry", LandRegistry)
  private val licenceButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.Licence", Licence)
  private val serviceStatementButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.ServiceStatement", ServiceStatement)
  private val stampDutyButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.StampDuty", StampDuty)
  private val utilityBillButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.UtilityBill", UtilityBill)
  private val waterRateButton: NGRRadioButtons = NGRRadioButtons("uploadEvidence.WaterRate", WaterRate)
  private val ngrRadio: NGRRadio = NGRRadio(NGRRadioName("upload-evidence-radio"), Seq(leaseButton, landRegistryButton,
    licenceButton, serviceStatementButton, stampDutyButton, utilityBillButton, waterRateButton))

  def show: Action[AnyContent] = {
    (authenticate andThen mandatoryCheck).async { implicit request: AuthenticatedUserRequest[AnyContent] =>
      propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
        case Some(property) =>
          val preparedForm: Form[UploadEvidenceForm] = property.uploadEvidence match {
            case None => form
            case Some(uploadEvidence) => form.fill(UploadEvidenceForm(uploadEvidence))
          }
          Future.successful(Ok(uploadEvidenceView(createDefaultNavBar(), form, buildRadios(preparedForm, ngrRadio), property.vmvProperty.addressFull)))
        case None => Future.failed(throw new NotFoundException("failed to find property from mongo"))
      }
    }
  }

  def submit: Action[AnyContent] =
    (authenticate andThen mandatoryCheck).async { implicit request =>
      form.bindFromRequest()
        .fold(
          formWithErrors => {
            propertyLinkingRepo.findByCredId(CredId(request.credId.getOrElse(""))).flatMap {
              case Some(property) =>
                Future.successful(BadRequest(
                  uploadEvidenceView(createDefaultNavBar(), formWithErrors, buildRadios(formWithErrors, ngrRadio), property.vmvProperty.addressFull)
                ))
              case None => Future.failed(throw new NotFoundException("failed to find property from mongo"))
            }
          },
          uploadEvidenceForm => {
            auditingService.extendedAudit(UploadEvidenceAuditModel(request.credId.getOrElse(""), uploadEvidenceForm, "upload-business-rates-bill"),
              uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes.UploadEvidenceController.show.url)
            val evidence = uploadEvidenceForm.radioValue
            propertyLinkingRepo.insertUploadEvidence(CredId(request.credId.getOrElse("")), evidence)
            Future.successful(Redirect(routes.UploadBusinessRatesBillController.show(None, Some(evidence))))
          }
        )
    }
}

