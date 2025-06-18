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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models

import play.api.libs.json.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.HttpUrlFormat

import java.net.URL
import java.time.Instant
import registration.CredId

case class UpscanReference(value: String)

object UpscanReference {
  implicit val referenceReader: Reads[UpscanReference] = Reads.StringReads.map(UpscanReference(_))
  implicit val referenceWrites: Writes[UpscanReference] = Writes.StringWrites.contramap(_.value)
}

case class UploadForm(href: String, fields: Map[String, String])

case class UpscanInitiateResponse(reference: UpscanReference, uploadRequest: UploadForm)

object UpscanInitiateResponse {

  implicit val uploadFormFormat: Format[UploadForm] = Json.format[UploadForm]

  implicit val format: Format[UpscanInitiateResponse] = Json.format[UpscanInitiateResponse]
}

case class UpscanInitiateRequest(
                                  callbackUrl: String,
                                  successRedirect: Option[String] = None,
                                  errorRedirect: Option[String] = None,
                                  minimumFileSize: Option[Int] = None,
                                  maximumFileSize: Option[Int] = None
                                )

object UpscanInitiateRequest {
  implicit val format: OFormat[UpscanInitiateRequest] = Json.format[UpscanInitiateRequest]
}

//case class UploadKey private (userId: String, srn: Srn) {
//  val value: String = userId + UploadKey.separator + srn.value
//}

//object UploadKey {
//  def fromRequest()(implicit req: DataRequest[_]): UploadKey =
//    UploadKey(req.getUserId)
//
//  val separator = "&&"
//}

case class UpscanFileReference(reference: String)

import play.api.data.FormError

case class UploadViewModel(
                           // detailsContent: DisplayMessage,
                            acceptedFileType: String,
                            maxFileSize: String,
                            formFields: Map[String, String],
                            error: Option[FormError]
                          )

object UploadStatus {

  sealed trait UploadStatus // needs to be in the same closure as its subtypes for Json.format to work

  case object InProgress extends UploadStatus

  case class Failed(failureDetails: UpscanCallBackErrorDetails) extends UploadStatus

  case class Success(name: String, mimeType: String, downloadUrl: String, size: Option[Long]) extends UploadStatus

}

sealed trait UpscanCallback {
  def reference: UpscanReference
}

case class UpscanCallbackSuccess(
                                  reference: UpscanReference,
                                  downloadUrl: URL,
                                  uploadDetails: UpscanCallbackUploadDetails
                            ) extends UpscanCallback

case class UpscanCallbackFailure(
                                  reference: UpscanReference,
                                  failureDetails: UpscanCallBackErrorDetails
                             ) extends UpscanCallback

case class UpscanCallbackUploadDetails(
                                  uploadTimestamp: Instant,
                                  checksum: String,
                                  fileMimeType: String,
                                  fileName: String,
                                  size: Long
                                )

case class UpscanRecord(credId: CredId,
                        reference: UpscanReference,
                        status: String,
                        downloadUrl: Option[String],
                        fileName: Option[String],
                        failureReason: Option[String],
                        failureMessage: Option[String])


object UpscanRecord {
  implicit val format: Format[UpscanRecord] = Json.format[UpscanRecord]
}


case class UpscanCallBackErrorDetails(failureReason: String, message: String)

object UpscanCallback {
  // must be in scope to create Reads for ReadyCallbackBody
  private implicit val urlFormat: Format[URL] = HttpUrlFormat.format

  implicit val uploadDetailsReads: Reads[UpscanCallbackUploadDetails] = Json.reads[UpscanCallbackUploadDetails]

  implicit val errorDetailsReads: Reads[UpscanCallBackErrorDetails] = Json.reads[UpscanCallBackErrorDetails]

  implicit val readyCallbackBodyReads: Reads[UpscanCallbackSuccess] = Json.reads[UpscanCallbackSuccess]

  implicit val failedCallbackBodyReads: Reads[UpscanCallbackFailure] = Json.reads[UpscanCallbackFailure]

  implicit val reads: Reads[UpscanCallback] = (json: JsValue) =>
    json \ "fileStatus" match {
      case JsDefined(JsString("READY")) => implicitly[Reads[UpscanCallbackSuccess]].reads(json)
      case JsDefined(JsString("FAILED")) => implicitly[Reads[UpscanCallbackFailure]].reads(json)
      case JsDefined(value) => JsError(s"Invalid type distriminator: $value")
      case JsUndefined() => JsError(s"Missing type distriminator")
      case _ => JsError(s"Missing type distriminator")
    }
}

//case class UploadDetails(
//                          //key: UploadKey,
//                          reference: Reference,
//                          status: UploadStatus.UploadStatus,
//                          lastUpdated: Instant
//                        )


