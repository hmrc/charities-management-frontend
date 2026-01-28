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

package controllers.actions

import com.google.inject.ImplementedBy
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import controllers.actions.AuthorisedAction.*
import models.requests.{AuthorisedRequest, CharityUser, UserType}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[DefaultClaimsAuthorisedAction])
trait ClaimsAuthorisedAction extends ActionBuilder[AuthorisedRequest, AnyContent] with ActionFunction[Request, AuthorisedRequest]

@Singleton
class DefaultClaimsAuthorisedAction @Inject() (
  override val authConnector: AuthConnector,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends ClaimsAuthorisedAction
    with AuthorisedFunctions {

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] = {

    given HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised().retrieve(Retrievals.affinityGroup.and(Retrievals.allEnrolments)) {
      case Some(affinityGroup @ (AffinityGroup.Agent | AffinityGroup.Organisation)) ~ enrolments =>
        val (enrolmentKey, identifierName, userType) =
          affinityGroup match {
            case AffinityGroup.Agent        => (AGENT_ENROLMENT_KEY, AGENT_IDENTIFIER_NAME, UserType.Agent)
            case AffinityGroup.Organisation => (ORG_ENROLMENT_KEY, ORG_IDENTIFIER_NAME, UserType.Organisation)
          }

        AuthorisedAction
          .getEnrolmentId(enrolments, enrolmentKey, identifierName)
          .map { uId =>
            block(AuthorisedRequest(request, CharityUser(userType, Some(uId))))
          }
          .getOrElse {
            Future.failed(UnsupportedAffinityGroup(s"${userType.toString} enrolment missing or not activated"))
          }
      case _ =>
        block(AuthorisedRequest(request, CharityUser(UserType.Individual, None)))
    }
  }
}

object AuthorisedAction {
  val ORG_ENROLMENT_KEY     = "HMRC-CHAR-ORG"
  val ORG_IDENTIFIER_NAME   = "CHARID"
  val AGENT_ENROLMENT_KEY   = "HMRC-CHAR-AGENT"
  val AGENT_IDENTIFIER_NAME = "AGENTCHARID"

  def getEnrolmentId(enrolments: Enrolments, enrolmentKey: String, identifierName: String): Option[String] = {
    enrolments.getEnrolment(enrolmentKey).flatMap(_.getIdentifier(identifierName)) match {
      case Some(enrolment) => Some(enrolment.value)
      case None            => None
    }
  }
}
