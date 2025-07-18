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

import play.api.Configuration
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.features.Features
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

trait AppConfig {
  val features: Features
  val nextGenerationRatesHost: String
  val ngrLoginRegistrationHost: String
  val ngrPropertyLinkingFrontendUrl: String
  val ngrPropertyLinkingFrontendInternalUrl: String
  val addressLookupUrl: String
  val upscanHost: String
  val ngrDashboardUrl: String
  val ngrLogoutUrl: String
  val ngrStubHost: String
  def getString(key: String): String
  val timeToLive: String
  val callbackEndpointTarget: String
  val customCurrentDate: Option[String] // TODO remove this after 1st April 2026, as it is only used for testing purposes
}

@Singleton
class FrontendAppConfig @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {
  override val features = new Features()(config)
  override val nextGenerationRatesHost: String = servicesConfig.baseUrl("next-generation-rates")
  override val ngrLoginRegistrationHost: String = servicesConfig.baseUrl("ngr-login-register-frontend")
  override val addressLookupUrl: String = servicesConfig.baseUrl("voa-modernised-api")
  override val ngrPropertyLinkingFrontendUrl: String = s"$propertyLinkingHost/ngr-property-linking-frontend"
  override val ngrPropertyLinkingFrontendInternalUrl: String = s"$propertyLinkingHost/internal"
  override val upscanHost: String = servicesConfig.baseUrl("upscan")
  override val ngrDashboardUrl: String = s"$dashboardHost/ngr-dashboard-frontend/dashboard"
  override val ngrLogoutUrl: String = s"$dashboardHost/ngr-dashboard-frontend/signout"
  override val ngrStubHost: String = getString("microservice.services.ngr-stub.host")
  override val timeToLive: String = servicesConfig.getString("time-to-live.time")
  override val callbackEndpointTarget: String = getString("upscan.callback-endpoint")
  override val customCurrentDate: Option[String] = config.getOptional("custom-current-date")
  def getString(key: String): String =
    config.getOptional[String](key).filter(!_.isBlank).getOrElse(throwConfigNotFoundError(key))

  private def throwConfigNotFoundError(key: String): String =
    throw new RuntimeException(s"Could not find config key '$key'")

  lazy val dashboardHost: String = getString("microservice.services.ngr-dashboard-frontend.host")
  lazy val propertyLinkingHost: String = getString("microservice.services.ngr-property-linking-frontend.host")

}
