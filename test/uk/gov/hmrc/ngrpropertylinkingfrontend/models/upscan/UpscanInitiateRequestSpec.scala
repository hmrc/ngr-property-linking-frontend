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

package uk.gov.hmrc.ngrpropertylinkingfrontend.models.upscan

import play.api.libs.json.Json
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport

class UpscanInitiateRequestSpec extends TestSupport{

  "UpscanInitiateRequest" should {

    "serialize to json" in {
      println(Json.toJson(upScanInitiateRequestModel))
      Json.toJson(upScanInitiateRequestModel) mustBe upscanInitiateRequestJson
    }

    "deserialize from json" in {
      upscanInitiateRequestJson.as[UpscanInitiateRequest] mustBe upScanInitiateRequestModel
    }

  }

}
