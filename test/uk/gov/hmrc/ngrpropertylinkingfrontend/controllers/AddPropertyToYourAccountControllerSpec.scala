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

import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{OK, SEE_OTHER}
import play.api.mvc.RequestHeader
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.AddPropertyToYourAccountView

class AddPropertyToYourAccountControllerSpec extends ControllerSpecSupport with DefaultAwaitTimeout {
  implicit val requestHeader: RequestHeader = mock[RequestHeader]
  lazy val addPropertyView: AddPropertyToYourAccountView = inject[AddPropertyToYourAccountView]
  val pageTitle = "Add a property to your account"

  def controller() = new AddPropertyToYourAccountController(
    addPropertyView,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockIsPropertyLinkedCheck,
    mcc
  )(appConfig = mockConfig)

  "AddPropertyToYourAccountController" must {
    "method show" must {
      "Return OK and the correct view" in {
        val result = controller().show()(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include(pageTitle)
      }
    }

    "method submit" must {
      "Return OK and the correct view" in {
        val result = controller().submit()(authenticatedFakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.WhatYouNeedController.show.url)
      }
    }
  }
}
