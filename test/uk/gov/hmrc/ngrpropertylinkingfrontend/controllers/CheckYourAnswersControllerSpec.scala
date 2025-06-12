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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.shouldBe
import play.api.http.Status.{CREATED, OK, SEE_OTHER}
import play.api.test.DefaultAwaitTimeout
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.{ControllerSpecSupport, TestData}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.NGRSummaryListRow.summarise
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.{NavBarContents, NavBarCurrentPage, NavBarPageContents, NavigationBarContent}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{Link, NGRSummaryListRow, PropertyLinkingUserAnswers}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends ControllerSpecSupport with TestData with DefaultAwaitTimeout{
  lazy val view: CheckYourAnswersView = inject[CheckYourAnswersView]
  lazy val propertyLinkingUserAnswers: PropertyLinkingUserAnswers = PropertyLinkingUserAnswers(credId = credId, vmvProperty = properties1.properties.head, currentRatepayer =  Some("Before"))

  def controller() = new CheckYourAnswersController(
    view,
    mockAuthJourney,
    mockIsRegisteredCheck,
    mockPropertyLinkingRepo,
    mockNgrConnector,
    mcc)

  val cyaSummary: Seq[SummaryListRow] = Seq(
    NGRSummaryListRow(
      messages("checkYourAnswers.property.title"),
      None,
      Seq("(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY"),
      changeLink = Some(Link(href = routes.FindAPropertyController.show, linkId = "property-address", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-address")))
    ),
    NGRSummaryListRow(
      messages("checkYourAnswers.currentRatepayer.title"),
      None,
      Seq("2191322564521"),
      None
    ),
    NGRSummaryListRow(
      messages("checkYourAnswers.currentRatepayer.title"),
      None,
      Seq("checkYourAnswers.currentRatepayer.before"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "current-ratepayer", messageKey = "service.change", visuallyHiddenMessageKey = Some("current-ratepayer")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.businessRatesBill"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "business-rates-bill", messageKey = "service.change", visuallyHiddenMessageKey = Some("business-rates-bill")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.EvidenceDocument"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "evidence-document", messageKey = "service.change", visuallyHiddenMessageKey = Some("evidence-document")))
    ), //TODO CHANGE CURRENT RATEPAYER
    NGRSummaryListRow(
      messages("checkYourAnswers.PropertyConnection"),
      None,
      Seq("userAnswers.credId.value.toString"),
      changeLink = Some(Link(href = routes.CurrentRatepayerController.show("CYA"), linkId = "property-connection", messageKey = "service.change", visuallyHiddenMessageKey = Some("property-connection")))
    ) //TODO CHANGE CURRENT RATEPAYER
  ).map(summarise)

  val content: NavigationBarContent = NavBarPageContents.CreateNavBar(
    contents = NavBarContents(
      homePage = Some(true),
      messagesPage = Some(false),
      profileAndSettingsPage = Some(false),
      signOutPage = Some(true)
    ),
    currentPage = NavBarCurrentPage(homePage = true),
    notifications = Some(1)
  )

  "Controller" must {
    "return OK and the correct view for a GET" in {
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      val result = controller().show()(authenticatedFakeRequest)
      status(result) mustBe OK
    }

    "Correctly display summary information" in {
      when(mockPropertyLinkingRepo.findByCredId(any())).thenReturn(Future.successful(Some(propertyLinkingUserAnswers)))
      val result = controller().show()(authenticatedFakeRequest)
      val content = contentAsString(result)
      content must include("Property to add to account")
      content must include("(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY")
      content must include("Property reference")
      content must include("2191322564521")
      content must include("Property reference")
      content must include("2191322564521")
    }
    "Calling the submit function return a 303 and the correct redirect location" in {
      mockRequest()
      val httpResponse = HttpResponse(CREATED, "Created Successfully")
      when(mockNgrConnector.upsertPropertyLinkingUserAnswers(any())(any())).thenReturn(Future.successful(httpResponse))
      val result = controller().submit()(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.WhatYouNeedController.show(Some("")).url)
    }
  }
}
