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

import org.mockito.Mockito.when
import play.api.http.Status.OK
import play.api.test.Helpers.{contentAsString, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.UpscanFileReference
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.UploadBusinessRatesBillView
import play.api.test.Helpers.defaultAwaitTimeout

import scala.concurrent.Future

class UploadBusinessRatesBillControllerSpec extends ControllerSpecSupport {
  val pageTitle = "Upload your business rates bill"
  val view: UploadBusinessRatesBillView = inject[UploadBusinessRatesBillView]
  val controller: UploadBusinessRatesBillController = new UploadBusinessRatesBillController(view, mockUpscanConnector, mockAuthJourney, mockIsRegisteredCheck, mcc)(mockConfig)

  "Upload business rates bill controller" must {
    "method show" must {
      "Return OK and the correct view" in {
        when(mockUpscanConnector.initiate(headerCarrier)).thenReturn(Future.successful(Right(UpscanFileReference("testRef"), "postTarget", Map.empty)))
        val result = controller.show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }
  }
}
