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

//package uk.gov.hmrc.ngrpropertylinkingfrontend.controllers
//
//
//package controllers
//
//import org.apache.hc.core5.http2.impl.H2Processors.client
//import org.scalatest.wordspec.AnyWordSpec
//import org.scalatest.matchers.must.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito.*
//import play.api.test.Helpers.*
//import play.api.test.{DefaultAwaitTimeout, FakeRequest, Helpers}
//import play.api.libs.json.{JsObject, Json, Writes}
//
//import scala.concurrent.{ExecutionContext, Future}
//import uk.gov.hmrc.auth.core.AuthConnector
//import play.api.mvc.{ControllerComponents, MessagesControllerComponents}
//import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
//import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, OptionValues}
//import play.api.http.Status.*
//import play.api.mvc.Results
//import org.mockito.ArgumentMatchers.any
//import org.scalatestplus.play.PlaySpec
//import org.scalatestplus.play.guice.GuiceOneServerPerSuite
//import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
//import uk.gov.hmrc.http.HeaderNames
//import uk.gov.hmrc.ngrpropertylinkingfrontend.models.sdes.SdesCallback
//import uk.gov.hmrc.ngrpropertylinkingfrontend.services.SdesService
//
//import java.util.UUID
//
//
//class SdesControllerSpec extends PlaySpec
//  with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience
//  with BeforeAndAfterEach with BeforeAndAfterAll with DefaultAwaitTimeout {
//
//  val testNonRepudiationApiKey = "testNonRepudiationApiKey"
//  override lazy val additionalConfig = Map("microservice.services.non-repudiation.api-key" -> testNonRepudiationApiKey)
//
//  val url: String = routes.SdesController.sdesCallback.url
//
//  val testNotification = "FileReceived"
//  val testFilename = "uploadedFilename.doc"
//  val testChecksumAlgorithm = "SHA2"
//  override val testChecksum = "23aab10f02dd6ca07bfdf270252904d754bcc844bf3ac1f52bbaa3b14126e266"
//  val testCorrelationID: String = UUID.randomUUID().toString
//  val testAvailableUntilString = "2021-01-06T10:01:00.889Z"
//  val testFailureReason = "Virus Detected"
//  val testDateTimeString = "2021-01-01T10:01:00.889Z"
//  override val testMimeType = "application/pdf"
//  val testNrsSubmissionId: String = UUID.randomUUID().toString
//  val testAttachmentId: String = UUID.randomUUID().toString
//  override val testFormBundleId = "1234123451234"
//  val testLocation = "s3://bucketname/path/to/file/in/upscan"
//  val testCallbackJson: JsObject = Json.obj(
//    "notification" -> testNotification,
//    "filename" -> s"$testFormBundleId-$testFilename",
//    "checksumAlgorithm" -> testChecksumAlgorithm,
//    "checksum" -> testChecksum,
//    "correlationID" -> testCorrelationID,
//    "availableUntil" -> testAvailableUntilString,
//    "failureReason" -> testFailureReason,
//    "dateTime" -> testDateTimeString,
//    "properties" -> Json.arr(
//      Json.obj(
//        "name" -> "location",
//        "value" -> testLocation
//      ),
//      Json.obj(
//        "name" -> "mimeType",
//        "value" -> testMimeType
//      ),
//      Json.obj(
//        "name" -> "nrsSubmissionId",
//        "value" -> testNrsSubmissionId
//      ),
//      Json.obj(
//        "name" -> "attachmentId",
//        "value" -> testAttachmentId
//      ),
//      Json.obj(
//        "name" -> "formBundleId",
//        "value" -> testFormBundleId
//      ),
//      Json.obj(
//        "name" -> "location",
//        "value" -> testLocation
//      )
//    )
//  )
//
////  val testNonRepudiationAttachmentId: String = UUID.randomUUID().toString
////  val testNrsPayload: NonRepudiationAttachment = NonRepudiationAttachment(
////    attachmentUrl = testLocation,
////    attachmentId = testAttachmentId,
////    attachmentSha256Checksum = testChecksum,
////    attachmentContentType = testMimeType,
////    nrSubmissionId = testNrsSubmissionId
////  )
//
//  lazy val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
//
////  def client(path: String): WSRequest = ws.url(s"http://localhost:$port/vatreg${path.replace("/vatreg", "")}")
////    .withHttpHeaders(HeaderNames.authorisation -> "test")
////    .withHttpHeaders("authorization" -> testAuthToken)
////    .withFollowRedirects(false)
//
//
////  s"POST $url" must {
////    "return OK for FileReceived notifications after successfully parsing the callback json and calling NRS" in  {
////      
////      val res: WSResponse = await(client(url).post(testCallbackJson - "failureReason"))
////
////      res.status mustBe ACCEPTED
////    }
//
////    "return OK for other notifications and not call NRS" in new SetupHelper {
//// 
////      stubMergedAudit(OK)
////
////      val callbackRequest = testCallbackJson.deepMerge(Json.obj("notification" -> "FileProcessed")) - "failureReason"
////      val res: WSResponse = await(client(url).post(callbackRequest))
////
////      res.status mustBe ACCEPTED
////    }
////
////    "return OK and audit after successfully parsing the callback auditing a failure from SDES" in new SetupHelper {
////  
////      
////      val res: WSResponse = await(client(url).post(testCallbackJson))
////
////      res.status mustBe ACCEPTED
////    }
//  }
//}
//
