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

import config.AppConfig
import models.requests.{AuthorisedRequest, CharityUser, UserType}
import play.api.mvc.*
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

trait BaseAuthorisedAction
    extends ActionBuilder[AuthorisedRequest, AnyContent]
    with ActionFunction[Request, AuthorisedRequest]
    with AuthorisedFunctions {

  def authConnector: AuthConnector
  def config: AppConfig
  def parser: BodyParser[AnyContent]
  implicit def executionContext: ExecutionContext

  private def hc(implicit req: Request[?]): HeaderCarrier =
    HeaderCarrierConverter.fromRequestAndSession(req, req.session)

  protected def accessDenied: Future[Result] =
    Future.successful(Results.Redirect(controllers.routes.AccessDeniedController.onPageLoad))

  protected def authorisedBlock[A](request: Request[A])(f: (Option[AffinityGroup], Enrolments) => Future[Result]): Future[Result] = {
    given HeaderCarrier = hc(request)

    authorised()
      .retrieve(Retrievals.affinityGroup and Retrievals.allEnrolments) { case affinityGroup ~ enrolments =>
        f(affinityGroup, enrolments)
      }
      .recover { case _: AuthorisationException =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      }
  }
}

@Singleton
@Named("agentAuth")
class AgentClaimsAuthorisedAction @Inject() (
  override val authConnector: AuthConnector,
  override val config: AppConfig,
  override val parser: BodyParsers.Default
)(implicit override val executionContext: ExecutionContext)
    extends BaseAuthorisedAction {

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] =
    authorisedBlock(request) { (affinityGroup, enrolments) =>
      affinityGroup match
        case Some(AffinityGroup.Agent) =>
          BaseAuthorisedAction
            .agentEnrolmentId(enrolments)
            .fold(accessDenied)(agentId => block(AuthorisedRequest(request, CharityUser(UserType.Agent, Some(agentId)))))

        case _ =>
          accessDenied
    }
}

@Singleton
@Named("orgAuth")
class OrgClaimsAuthorisedAction @Inject() (
  override val authConnector: AuthConnector,
  override val config: AppConfig,
  override val parser: BodyParsers.Default
)(implicit override val executionContext: ExecutionContext)
    extends BaseAuthorisedAction {

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] =
    authorisedBlock(request) { (affinityGroup, enrolments) =>
      affinityGroup match
        case Some(AffinityGroup.Organisation) =>
          BaseAuthorisedAction
            .orgEnrolmentId(enrolments)
            .fold(accessDenied)(orgId => block(AuthorisedRequest(request, CharityUser(UserType.Organisation, Some(orgId)))))

        case _ =>
          accessDenied
    }
}

@Singleton
@Named("identifyAuth")
class IdentifyClaimsAuthAction @Inject() (
  override val authConnector: AuthConnector,
  override val config: AppConfig,
  override val parser: BodyParsers.Default
)(implicit override val executionContext: ExecutionContext)
    extends BaseAuthorisedAction {

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] =
    authorisedBlock(request) { (affinityGroup, enrolments) =>
      val user = affinityGroup match
        case Some(AffinityGroup.Agent) =>
          CharityUser(UserType.Agent, BaseAuthorisedAction.agentEnrolmentId(enrolments))

        case Some(AffinityGroup.Organisation) =>
          CharityUser(UserType.Organisation, BaseAuthorisedAction.orgEnrolmentId(enrolments))

        case _ =>
          CharityUser(UserType.Individual, None)

      block(AuthorisedRequest(request, user))
    }
}

object BaseAuthorisedAction {
  private val OrgEnrolmentKey     = "HMRC-CHAR-ORG"
  private val OrgIdentifierName   = "CHARID"
  private val AgentEnrolmentKey   = "HMRC-CHAR-AGENT"
  private val AgentIdentifierName = "AGENTCHARID"

  private def enrolmentId(enrolments: Enrolments, key: String, name: String): Option[String] =
    enrolments.getEnrolment(key).flatMap(_.getIdentifier(name)).map(_.value)

  def agentEnrolmentId(enrolments: Enrolments): Option[String] = BaseAuthorisedAction
    .enrolmentId(enrolments, BaseAuthorisedAction.AgentEnrolmentKey, BaseAuthorisedAction.AgentIdentifierName)

  def orgEnrolmentId(enrolments: Enrolments): Option[String] = BaseAuthorisedAction
    .enrolmentId(enrolments, BaseAuthorisedAction.OrgEnrolmentKey, BaseAuthorisedAction.OrgIdentifierName)
}
