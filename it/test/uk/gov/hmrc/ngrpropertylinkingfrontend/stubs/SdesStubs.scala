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

package uk.gov.hmrc.ngrpropertylinkingfrontend.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, containing, equalTo, post, stubFor, urlEqualTo, urlPathEqualTo}
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.NO_CONTENT
import play.api.libs.json.{JsValue, Json}

object SdesStubs {


  def fileReadyStub: Unit = {
    stubFor(
      post(urlPathEqualTo("/notification/fileready"))
        .willReturn(aResponse().withStatus(204)) // or 200 depending on your test
    )
  }
  
  


  def stubSdesNotification(expectedJson: JsValue)(status: Int): Unit = {
    stubFor(
      post(urlPathEqualTo("/notification/fileready"))
        .withHeader("Csrf-Token", equalTo("nocheck"))
        .withHeader("x-client-id", equalTo("test-token"))
        .withHeader("Content-Type", containing("application/json"))
        .withRequestBody(new EqualToJsonPattern(Json.stringify(expectedJson), true, true))
        .willReturn(aResponse().withStatus(status).withBody(""))
    )
  }


}

