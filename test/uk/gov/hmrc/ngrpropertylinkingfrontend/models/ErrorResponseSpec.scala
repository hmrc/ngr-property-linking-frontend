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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class ErrorResponseSpec extends TestSupport {

  val testErrorResponse: ErrorResponse = ErrorResponse(code = 400, message = "Bad request")

  val testErrorResponseJson: JsValue = Json.parse(
    """{"code": 400,
      |"message": "Bad request"
      |}""".stripMargin)
  
  "ErrorResponse" should {
    "deserialize to json" in {
      Json.toJson(testErrorResponse) mustBe testErrorResponseJson
    }
    "serialize to json" in {
      testErrorResponseJson.as[ErrorResponse] mustBe testErrorResponse
    }
  }
}
