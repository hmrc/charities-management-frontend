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
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.*
import uk.gov.hmrc.auth.core.AuthConnector

import scala.concurrent.{ExecutionContext, Future}

class FakeClaimsAuthorisedAction(
  cc: ControllerComponents,
  user: CharityUser
)(implicit ec: ExecutionContext)
    extends BaseAuthorisedAction {

  override val authConnector: AuthConnector = mock[AuthConnector]

  override val config: AppConfig = mock[AppConfig]

  override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

  override implicit def executionContext: ExecutionContext = ec

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] =
    block(AuthorisedRequest(request, user))
}

object FakeAuthorisedAction {

  def agent(
    cc: ControllerComponents,
    id: String = "agent-id"
  )(implicit ec: ExecutionContext): FakeClaimsAuthorisedAction =
    new FakeClaimsAuthorisedAction(
      cc,
      CharityUser(UserType.Agent, Some(id))
    )

  def org(
    cc: ControllerComponents,
    id: String = "org-id"
  )(implicit ec: ExecutionContext): FakeClaimsAuthorisedAction =
    new FakeClaimsAuthorisedAction(
      cc,
      CharityUser(UserType.Organisation, Some(id))
    )

  def individual(
    cc: ControllerComponents
  )(implicit ec: ExecutionContext): FakeClaimsAuthorisedAction =
    new FakeClaimsAuthorisedAction(
      cc,
      CharityUser(UserType.Individual, None)
    )
}
