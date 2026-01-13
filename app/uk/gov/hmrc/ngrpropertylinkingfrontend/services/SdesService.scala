/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.SdesConnector
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.PropertyExtractor.{attachmentReferenceKey, locationKey, mimeTypeKey, nrsSubmissionKey}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.UploadedFile
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.PropertyLinkingRepo

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class SdesService @Inject() (
                              sdesConnector: SdesConnector,
                              auditService: AuditingService,
                              ngrLogger: NGRLogger
                            )(implicit executionContext: ExecutionContext, appConfig: AppConfig) {
  
  def processCallback(sdesCallback: SdesCallback)(implicit hc: HeaderCarrier, request: Request[_]): Future[Unit] = {
    println(Console.GREEN + "processCallback" + Console.RESET)
    val optUrl = sdesCallback.getPropertyValue(locationKey)
    val optAttachmentId = sdesCallback.getPropertyValue(attachmentReferenceKey)
    val optMimeType = sdesCallback.getPropertyValue(mimeTypeKey)
    val optNrSubmissionId = sdesCallback.getPropertyValue(nrsSubmissionKey)

    ngrLogger.info(
      s"[SdesService][processCallback] Attempting to process callback" +
        s"\n optAttachmentId: $optAttachmentId" +
        s"\n optNrSubmissionId: $optNrSubmissionId" +
        s"\n SDES notification status: ${sdesCallback.notification}"
    )

    (optUrl, optAttachmentId, optMimeType, optNrSubmissionId, sdesCallback.checksum, sdesCallback.failureReason) match {
      case (Some(url), Some(attachmentId), Some(mimeType), Some(nrSubmissionId), Some(checksum), None)
        if sdesCallback.notification == SdesService.fileReceived =>

        val payload = SdesFileReturn(
          attachmentUrl = url,
          attachmentId = attachmentId,
          attachmentSha256Checksum = checksum,
          attachmentContentType = mimeType,
          nrSubmissionId = nrSubmissionId
        )

        println(Console.MAGENTA_B + s"[SdesService] Received SDES fileReceived callback for attachment $attachmentId" + Console.RESET)
        Future.successful(ngrLogger.info(s"[SdesService] Received SDES fileReceived callback for attachment $attachmentId"))
      //          auditService.audit(SdesCallbackSuccessAudit(sdesCallback, payload.attachmentUrl))


      case (Some(_), Some(attachmentId), Some(_), Some(_), Some(_), None) =>
        if (sdesCallback.notification != SdesService.fileProcessed) {
          //          pagerduty(
          //            PagerDutyKeys.UNEXPECTED_SDES_CALLBACK_STATUS,
          //            Some(s"[SdesService] Expected SDES callback status FileProcessed for $attachmentId, but was ${sdesCallback.notification}")
          //          )
        }
        Future.successful(
          ngrLogger.info(
            s"[SdesService] Not sending attachment NRS payload for $attachmentId. SDES notification type was ${sdesCallback.notification}"
          )
          //auditService.audit(SdesCallbackNotSentToNrsAudit(sdesCallback))
        )
      case (_, Some(attachmentId), _, _, _, Some(failureReason)) =>
        //        pagerduty(
        //          PagerDutyKeys.SDES_CALLBACK_FAILED,
        //          Some(s"[SdesService] Not sending attachment NRS payload as callback for $attachmentId failed with reason: $failureReason"))
        Future.successful(
          ngrLogger.info(
            s"[SdesService] Not sending attachment NRS payload as callback for $attachmentId failed with reason: $failureReason"
          )
          //auditService.audit(SdesCallbackFailureAudit(sdesCallback))
        )
      case (Some(_), Some(_), Some(_), None, Some(_), _) =>
        Future.successful(
          ngrLogger.info(
            s"[SdesService] Not sending attachment NRS payload as NRS failed for the Registration Submission"
          )
          //          pagerduty(
          //            PagerDutyKeys.SDES_NRS_SUBMISSION_ID_MISSING,
          //            Some("[SdesService] Not sending attachment NRS payload as NRS failed for the Registration Submission")
          //          )
        )
      case _ =>
        Future.successful(
          ngrLogger.info(
            s"[SdesService] Could not send attachment NRS payload due to missing data in the callback"
          )
          //          pagerduty(
          //            PagerDutyKeys.INVALID_SDES_PAYLOAD_RECEIVED,
          //            Some("[SdesService] Could not send attachment NRS payload due to missing data in the callback"))
        )
    }
  }
}

  object SdesService {
    val fileReceived = "FileReceived"
    val fileProcessed = "FileProcessed"
  }
