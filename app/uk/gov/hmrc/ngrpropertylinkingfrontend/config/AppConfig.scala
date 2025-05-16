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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.ngrpropertylinkingfrontend.config.features.Features
import uk.gov.hmrc.ngrpropertylinkingfrontend.controllers.routes

@Singleton
class AppConfig @Inject()(config: Configuration) {
  val features = new Features()(config)
  def getString(key: String): String =
    config.getOptional[String](key).filter(!_.isBlank).getOrElse(throwConfigNotFoundError(key))
  private def throwConfigNotFoundError(key: String): String =
    throw new RuntimeException(s"Could not find config key '$key'")

  lazy val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)
  private lazy val feedbackFrontendHost = getString("microservice.services.feedback-survey-frontend.host")
  private lazy val dashboardHost = getString("microservice.services.ngr-dashboard-frontend.host")
  private lazy val envHost = getString("environment.host")
  private lazy val basGatewayHost = getString("microservice.services.bas-gateway-frontend.host")

  private lazy val dashboardBeforeYouGoUrl = s"$envHost${routes.BeforeYouGoController.show().url}"
  lazy val dashboardHomeUrl = s"$dashboardHost/dashboard"
  lazy val feedbackFrontendUrl = s"$feedbackFrontendHost/feedback/NGR-Dashboard"
  lazy val logoutUrl: String = s"$basGatewayHost/bas-gateway/sign-out-without-state?continue=$dashboardBeforeYouGoUrl"

}
