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

package uk.gov.hmrc.ngrpropertylinkingfrontend.mocks

import play.api.Configuration
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.AppConfig
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.features.Features

class MockAppConfig(val runModeConfiguration: Configuration) extends AppConfig {
  override val appName: String = "ngr-login-register-frontend"
  override val features: Features = new Features()(runModeConfiguration)
  override val nextGenerationRatesHost: String = "https://localhost:1500"
  override val ngrLoginRegistrationHost: String = "https://localhost:1502"
  override val upscanHost: String = "http://localhost:9570"
  override val ngrDashboardUrl: String = "http://localhost:1503/ngr-dashboard-frontend/dashboard"
  override val ngrLogoutUrl: String = "http://localhost:1503/ngr-dashboard-frontend/signout"
  override val ngrCheckYourDetailsUrl: String = "http://localhost:1503/ngr-dashboard-frontend/check-your-details"
  override val ngrStubHost: String = "http://localhost:1501"
  override val timeToLive: String = "3"
  override val ngrPropertyLinkingFrontendInternalUrl: String = "http://localhost:1504/internal"
  override val ngrPropertyLinkingFrontendUrl: String = "http://localhost:1504/ngr-property-linking-frontend"
  override val callbackEndpointTarget: String = "http://localhost:1504/internal/callback-from-upscan"
  override val vmvAddressLookup: String = "http://localhost:9301"
  override val uploadRedirectTargetBase: String = "http://localhost:1504"
  override val ngrNotify: String = "https://localhost:1515"
  
  override def getString(key: String): String = ???

  override val customCurrentDate: Option[String] = None // TODO remove this after 1st April 2026, as it is only used for testing purposes
}

