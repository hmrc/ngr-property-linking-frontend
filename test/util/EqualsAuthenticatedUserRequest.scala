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

package util

import org.mockito.ArgumentMatcher
import org.scalactic.Prettifier
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.auth.AuthenticatedUserRequest

case class EqualsAuthenticatedUserRequest(expected: AuthenticatedUserRequest[_])
  extends ArgumentMatcher[AuthenticatedUserRequest[_]]
    with Matchers {

  class LocalPrettifier extends Prettifier {
    override def apply(o: Any): String =
      o match {
        case request: AuthenticatedUserRequest[_] =>
          s"AuthenticatedRequest { Original request: ${request.request}, authProvider: ${request.authProvider}, nino: ${request.nino}, email: ${request.email}, credId: ${request.credId}, affinityGroup: ${request.affinityGroup}, name: ${request.name}}"
        case _                                => o.toString
      }
  }

  override def matches(argument: AuthenticatedUserRequest[_]): Boolean = {
    withClue(s"Argument doesn't match: ") {
      argument mustBe expected
    }
    true
  }

}
