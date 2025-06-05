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
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ControllerSpecSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.vmv.{VMVProperties, VMVProperty, Valuation}
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.{ErrorResponse, Postcode}
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.{ErrorTemplate, SingleSearchResultView}

import java.time.LocalDate
import scala.concurrent.Future

class SingleSearchResultControllerSpec extends ControllerSpecSupport {

  lazy val singleSearchResultView: SingleSearchResultView = inject[SingleSearchResultView]
  lazy val errorView: ErrorTemplate = inject[ErrorTemplate]

  def controller() = new SingleSearchResultController(
    singleSearchResultView,
    errorView,
    mockAuthJourney,
    mockFindAPropertyConnector,
    mockIsRegisteredCheck,
    mcc
  )(mockConfig)

  val testPostcode = Postcode(value = "BH1 7EY")

  val properties1: VMVProperties = VMVProperties(total = 1,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      )
    )
  )

  val properties1WithMillionsPoundRateableValue: VMVProperties = VMVProperties(total = 1,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 2109300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      )
    )
  )

  val properties11: VMVProperties = VMVProperties(total = 11,
    properties = List(
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      ),
      VMVProperty(
        uarn = 11905603000L,
        localAuthorityReference = "2191322564521",
        addressFull = "(INCL STORE R/O 2 & 2A) 2A, RODLEY LANE, RODLEY, LEEDS, BH1 7EY",
        localAuthorityCode = "4720",
        valuations = List(
          Valuation(
            assessmentStatus = "CURRENT",
            assessmentRef = 25141561000L,
            rateableValue = 9300,
            scatCode = "249",
            currentFromDate = LocalDate.of(2023, 4, 1),
            effectiveDate = LocalDate.of(2023, 4, 1),
            descriptionText = "SHOP AND PREMISES",
            listYear = "2023",
            primaryDescription = "CS",
            allowedActions = List(
              "check",
              "challenge",
              "viewDetailedValuation",
              "propertyLink",
              "similarProperties"
            ),
            propertyLinkEarliestStartDate = LocalDate.of(2017, 4, 1),
            listType = "current"
          )
        )
      )
    )
  )


  "Address Search Result Controller" must {
    "method show" must {
      "Return OK and the correct view when theirs 1 address on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAProperty(any[Postcode]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Right(properties1)))
        val result = controller().show(page = 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>1</strong> of <strong>1</strong> items.")
      }
      "Return OK and the correct view with correct rateable value format" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAProperty(any[Postcode]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Right(properties1WithMillionsPoundRateableValue)))
        val result = controller().show(page = 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("£2,109,300")
      }
      "Return Ok and the correct view with paginate on page 1" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAProperty(any[Postcode]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Right(properties11)))
        val result = controller().show(page = 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>1</strong> to <strong>10</strong> of <strong>11</strong> items.")
        content must include("Next")
      }
      "Return Ok and the correct view with paginate on page 2" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAProperty(any[Postcode]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful(Right(properties11)))
        val result = controller().show(page = 2)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("Showing <strong>11</strong> to <strong>11</strong> of <strong>11</strong> items.")
        content must include("Previous")
      }
      "Return error page when vmv connector fails" in {
        mockConfig.features.vmvPropertyLookupTestEnabled(true)
        when(mockFindAPropertyConnector.findAProperty(any[Postcode]())(any[HeaderCarrier]()))
          .thenReturn(Future.successful( Left(ErrorResponse(BAD_REQUEST, s"Json Validation Error:"))))
        val result = controller().show(page = 1)(authenticatedFakeRequest)
        status(result) mustBe OK
        val content = contentAsString(result)
        content must include("""<h1 class="govuk-heading-xl">There is a problem</h1>""")
      }
    }
    "Return SEE OTHER and pass chosen property index to confirm your address page with mode as check your answers" in {
      val result = controller().selectedProperty(1)(authenticatedFakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SingleSearchResultController.show(1).url)
    }
  }

}
