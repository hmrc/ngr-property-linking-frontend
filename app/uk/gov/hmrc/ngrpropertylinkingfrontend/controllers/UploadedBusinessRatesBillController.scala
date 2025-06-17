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
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{PreparedUpload, UploadViewModel}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{UploadBusinessRatesBillView, UploadedBusinessRatesBillView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.forms.UploadForm

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadedBusinessRatesBillController @Inject()(
                                                   
                                                   
                                                  uploadedView: UploadedBusinessRatesBillView,
                                                  upscanConnector: UpscanConnector,
                                                  uploadForm: UploadForm,
                                                  authenticate: AuthRetrievals,
                                                  isRegisteredCheck: RegistrationAction,
                                                  mcc: MessagesControllerComponents)(implicit appConfig: AppConfig, ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

    def show: Action[AnyContent] =
      (authenticate andThen isRegisteredCheck).async { implicit request =>
        //val x = uploadedView("testFile.aaa", createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl)
        Future.successful(Ok(uploadedView("testFile.aaa", createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl)))
        //Future.successful(Ok("test"))
      }
}

//    def show2: Action[AnyContent] =
//      (authenticate andThen isRegisteredCheck).async { implicit request =>
//
//        for {
//          x <- upscanConnector.initiate
//        } yield {
//          Ok(view(form, x, createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl))
//        }
//      }

//  def showOLD: Action[AnyContent] =
//    (authenticate andThen isRegisteredCheck).async { implicit request =>
//
//      val uploadViewModel: Future[UploadViewModel] = upscanConnector.initiate.map { upscanInitiateResponse =>
//        UploadViewModel(
//          postTarget = upscanInitiateResponse.uploadRequest.href,
//          acceptedFileType = ???,
//          maxFileSize = "1000",
//          formFields = upscanInitiateResponse.uploadRequest.fields,
//          error = None
//        )
//      }
//
//      val form = uploadForm()
//
//      //val upscanInitiateResponse: Future[PreparedUpload] = upscanConnector.initiate
//
//      //TODO confirm back url
//      Future.successful(Ok(view(form, createDefaultNavBar, routes.FindAPropertyController.show.url, appConfig.ngrDashboardUrl)))
//    }

//    def form(): Form[PhoneNumber] =
//      Form(
//        mapping(
//          phoneNumber -> text()
//            .verifying(
//              firstError(
//                isNotEmpty(phoneNumber, phoneNumberEmptyError),
//                regexp(phoneNumberRegexPattern.pattern(), phoneNumberInvalidFormat)
//              )
//            )
//        )(PhoneNumber.apply)(PhoneNumber.unapply)
//      )


//  case class UploadViewModel(
//                              // detailsContent: DisplayMessage,
//                              postTarget: String,
//                              acceptedFileType: String,
//                              maxFileSize: String,
//                              formFields: Map[String, String],
//                              error: Option[FormError]
//                            )


//  def buildViewModel(
//                 postTarget: String,
//                 formFields: Map[String, String],
//                 error: Option[FormError],
//                 maxFileSize: String
//               ): FormPageViewModel[UploadViewModel] =
//    FormPageViewModel(
//      "uploadMemberDetails.title",
//      "uploadMemberDetails.heading",
//      UploadViewModel(
//        detailsContent =
//          ParagraphMessage("uploadMemberDetails.details.paragraph") ++
//            ListMessage(
//              ListType.Bullet,
//              "uploadMemberDetails.list1",
//              "uploadMemberDetails.list2",
//              "uploadMemberDetails.list3"
//            ),
//        acceptedFileType = ".csv",
//        maxFileSize = maxFileSize,
//        formFields,
//        error
//      ),
//      Call("POST", postTarget)
//    ).withDescription(ParagraphMessage("uploadMemberDetails.paragraph"))

