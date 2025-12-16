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

package uk.gov.hmrc.ngrpropertylinkingfrontend.services

import jakarta.inject.Inject
import org.scalatest.PrivateMethodTester
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.TestSupport
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.audit.ExtendedAuditModel
import uk.gov.hmrc.play.audit.http.connector.AuditConnector


class AuditingServiceSpec  extends TestSupport with PrivateMethodTester {

  "The AuditService" should {

    val auditConnector: AuditConnector = inject[AuditConnector]

    val obj = new AuditingService(mockConfig, auditConnector)
    val auditModel = new ExtendedAuditModel {
      override val auditType: String = "auditType"
      override val detail: Map[String,String] = Map("detail" -> "detail")

    }
    s"return ExtendedDataEvent" in {
      val result = obj.toExtendedDataEvent("appName", auditModel, "path")

      result.auditSource mustBe "appName"
      result.auditType mustBe  "auditType"
    }
  }

}