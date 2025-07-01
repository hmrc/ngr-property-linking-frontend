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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.connectors.UpscanConnector

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import play.api.data.FormError

@Singleton
class UpscanService  @Inject()(upscanConnector: UpscanConnector)(implicit ec: ExecutionContext, hc: HeaderCarrier) {
  
//  def prepareUploadForm(): Unit = {
//    val x = upscanConnector.initiate
//    val z: UploadViewModel = x.flatMap(a =>)
//
//  }

  case class UploadViewModel(
                              // detailsContent: DisplayMessage,
                              acceptedFileType: String,
                              maxFileSize: String,
                              formFields: Map[String, String],
                              error: Option[FormError]
                            )
  

}
