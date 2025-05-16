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

package uk.gov.hmrc.ngrpropertylinkingfrontend.config

import org.mockito.Mockito.when
import play.api.Configuration
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport
class AppConfigSpec extends TestSupport {
  "AppConfig" must {
    "retrieve logout url correct from config" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("microservice.services.ngr-dashboard-frontend.host")).thenReturn(Some("http://localhost:1503"))

      appConfig.logoutUrl mustBe ("http://localhost:1503/ngr-dashboard-frontend/signout")
    }

    "missing dashboard host from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("microservice.services.ngr-dashboard-frontend.host")).thenReturn(None)

      val exception = intercept[Exception] {
        appConfig.logoutUrl
      }
      exception.getMessage mustBe "Could not find config key 'microservice.services.ngr-dashboard-frontend.host'"
    }

    "empty dashboard host String from config throws exception" in {
      val mockConfig = mock[Configuration]
      val appConfig = new AppConfig(mockConfig)
      when(mockConfig.getOptional[String]("microservice.services.ngr-dashboard-frontend.host")).thenReturn(Some(""))

      val exception = intercept[Exception] {
        appConfig.logoutUrl
      }
      exception.getMessage mustBe "Could not find config key 'microservice.services.ngr-dashboard-frontend.host'"
    }
  }
}
