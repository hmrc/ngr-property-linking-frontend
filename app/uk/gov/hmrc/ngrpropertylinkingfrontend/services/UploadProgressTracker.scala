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
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscanV2.*

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.ngrpropertylinkingfrontend.repo.{UpscanRepo, UserSessionRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadProgressTracker @Inject()(
                                       repository: UserSessionRepository,
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

