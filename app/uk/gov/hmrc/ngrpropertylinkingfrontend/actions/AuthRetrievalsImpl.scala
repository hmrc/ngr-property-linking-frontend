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

package uk.gov.hmrc.ngrpropertylinkingfrontend.actions

import com.google.inject.{ImplementedBy, Inject}
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.{Credentials, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.auth.AuthenticatedUserRequest
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class AuthRetrievalsImpl @Inject()(
                                    val authConnector: AuthConnector,
                                    mcc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext) extends AuthRetrievals
  with AuthorisedFunctions {

  type RetrievalsType = Option[Credentials] ~ Option[String] ~ Option[AffinityGroup]

  override def invokeBlock[A](request: Request[A], block: AuthenticatedUserRequest[A] => Future[Result]): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals: Retrieval[RetrievalsType] = Retrievals.credentials and Retrievals.nino and Retrievals.affinityGroup

    authorised(ConfidenceLevel.L250).retrieve(retrievals){
      case credentials ~ Some(nino) ~ affinityGroup  =>
        block(
          AuthenticatedUserRequest(
            request = request,
            authProvider = credentials.map(_.providerType),
            nino = Nino(hasNino = true,Some(nino)),
            email = None,
            credId = credentials.map(_.providerId),
            affinityGroup = affinityGroup,
            name = None
          )
        )
      case _ => Future.failed(new RuntimeException())
    } recoverWith {
      case NonFatal(ex) =>
        throw ex
    }
  }
  // $COVERAGE-OFF$
  override def parser: BodyParser[AnyContent] = mcc.parsers.defaultBodyParser

  override protected def executionContext: ExecutionContext = ec
  // $COVERAGE-ON$
}

@ImplementedBy(classOf[AuthRetrievalsImpl])
trait AuthRetrievals extends ActionBuilder[AuthenticatedUserRequest, AnyContent] with ActionFunction[Request, Request]