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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import org.bson.types.ObjectId
import play.api.http.Status.CREATED
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.mvc.Results.BadRequest
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.logging.NGRLogger
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.registration.CredId
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan.*
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{FileUploadRepo, PropertyLinkingRepo}
import uk.gov.hmrc.ngrpropertylinkingfrontend.utils.UniqueIdGenerator
import uk.gov.hmrc.objectstore.client.play.PlayObjectStoreClient
import uk.gov.hmrc.objectstore.client.{Path, RetentionPeriod, Sha256Checksum}

import java.net.URL
import java.time.ZonedDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadProgressTracker @Inject()(
                                       repository: FileUploadRepo,
                                       appConfig: AppConfig,
                                       osClient  : PlayObjectStoreClient,
                                       propertyLinkingRepo: PropertyLinkingRepo,
                                       httpClient: HttpClientV2,
                                       logger: NGRLogger
                                     )(using
                                       ExecutionContext
                                     ):

  def requestUpload(uploadId: UploadId, fileReference: Reference): Future[Unit] =
    repository.insert(UploadDetails(ObjectId.get(), uploadId, fileReference, UploadStatus.InProgress))

  def registerUploadResult(fileReference: Reference, uploadStatus: UploadStatus)
                          (using hc: HeaderCarrier): Future[Unit] =
    for
      _ <- repository.updateStatus(fileReference, uploadStatus)
    yield
      ()

  def getUploadResult(id: UploadId): Future[Option[UploadStatus]] =
    repository
      .findByUploadId(id)
      .map(_.map(_.status))


  private def createClientAuthToken(): Future[Unit] = {
    logger.info("[InternalAuthTokenInitialiser][createClientAuthToken] Initialising auth token")
    httpClient
      .post(url"${appConfig.internalAuthService}/test-only/token")(HeaderCarrier())
      .withBody(
        Json.obj(
          "token" -> appConfig.internalAuthToken,
          "principal" -> appConfig.appName,
          "permissions" -> Seq(
            Json.obj(
              "resourceType" -> "object-store",
              "resourceLocation" -> "ngr-property-linking-frontend",
              "actions" -> List("READ", "WRITE", "DELETE")
            ),
            Json.obj(
              "resourceType" -> "business-rates-bill",
              "resourceLocation" -> "*",
              "actions" -> List("*")
            )
          )
        )
      )
      .execute
      .flatMap { response =>
        if (response.status == CREATED) {
          logger.info(
            "[InternalAuthTokenInitialiser][createClientAuthToken] Auth token initialised"
          )
          Future.successful("")
        } else {
          logger.error(
            "[InternalAuthTokenInitialiser][createClientAuthToken] Unable to initialise internal-auth token"
          )
          Future.failed(new RuntimeException("Unable to initialise internal-auth token"))
        }
      }
  }


  def transferToObjectStore(
                             credId: CredId,
                             downloadUrl: URL,
                             mimeType: String,
                             checksum: String,
                             evidenceDocument: String,
                             fileReference: Reference,
                             uploadStatus: UploadStatus,
                             appConfig: AppConfig
                           )(using hc: HeaderCarrier): Future[Unit] = {
    val fileLocation = Path.File(s"${fileReference.value}/${evidenceDocument}")
    val contentSha256 = Sha256Checksum.fromHex(checksum)
    createClientAuthToken()
    osClient
      .uploadFromUrl(
        from = url"${downloadUrl}",
        to = fileLocation,
        retentionPeriod = RetentionPeriod.OneDay,
        contentType = Some(mimeType),
        contentSha256 = Some(contentSha256)
      )(using hc.withExtraHeaders("Authorization" -> appConfig.internalAuthToken))
      .transformWith {
        case scala.util.Failure(exception) =>
          logger.error(s"Failure to store object because of $exception")
          exception.printStackTrace()
          Future.successful(BadRequest(s"Failure to store object because of $exception"))
        case scala.util.Success(objectWithMD5) =>
          osClient
            .presignedDownloadUrl(path = fileLocation)
            .transformWith {
              case scala.util.Failure(exception) =>
                logger.error(s"Failure to get pre-signed URL to $fileLocation because of $exception")
                exception.printStackTrace()
                Future.successful(
                  BadRequest(s"Failure to get pre-signed URL to $fileLocation because of $exception")
                )
              case scala.util.Success(presignedDownloadUrl) =>
                val ref = UniqueIdGenerator.generateId
                val uploadedFile =
                  UploadedFile(
                    ref = ref,
                    upscanReference = fileLocation.asUri,
                    downloadUrl = presignedDownloadUrl.downloadUrl.toExternalForm(),
                    uploadTimestamp = ZonedDateTime.now(),
                    fileSize = presignedDownloadUrl.contentLength.toInt
                  )
                Future.successful(
                  for {
                    referenceNumberInsert <- propertyLinkingRepo.insertReferenceNumber(credId, ref)
                    success <- referenceNumberInsert match {
                      case Some(result) => Future.successful(result)
                      case None => Future.failed(new Exception(s"Could not save reference for credId: ${credId.value}"))
                    }
                    uploadFile <-
                      propertyLinkingRepo.insertUploadedFile(
                        credId,
                        File(
                          recipientOrSender = Some("ngr-property-linking"),
                          name = ref,
                          location = Some(uploadedFile.downloadUrl),
                          Checksum(appConfig.sdesChecksumAlgorithm, checksum),
                          size = presignedDownloadUrl.contentLength.toInt,
                          properties = List.empty
                        )
                      )
                    success <- referenceNumberInsert match {
                      case Some(result) =>
                        Future.successful(result)
                      case None => Future.failed(new Exception(s"Could not save reference for credId: ${credId.value}"))
                    }
                  } yield success
                )
            }
      }
  }