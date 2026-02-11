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

import models.requests.{AuthorisedRequest, CharityUser, UserType}
import play.api.mvc.*
import play.api.test.Helpers.stubBodyParser

import scala.concurrent.{ExecutionContext, Future}

class FakeClaimsAuthorisedAction(
  user: CharityUser
)(implicit ec: ExecutionContext)
    extends ActionBuilder[AuthorisedRequest, AnyContent]
    with OrgAuthorisedAction
    with AgentAuthorisedAction
    with IdentifyAuthorisedAction {

  override def parser: BodyParser[AnyContent] =
    stubBodyParser(AnyContentAsEmpty)

  override protected def executionContext: ExecutionContext = ec

  override def invokeBlock[A](
    request: Request[A],
    block: AuthorisedRequest[A] => Future[Result]
  ): Future[Result] =
    block(AuthorisedRequest(request, user))
}

object FakeAuthorisedAction {

  def agent(id: String = "agent-id")(implicit ec: ExecutionContext) =
    new FakeClaimsAuthorisedAction(
      CharityUser(UserType.Agent, Some(id))
    )

  def org(id: String = "org-id")(implicit ec: ExecutionContext) =
    new FakeClaimsAuthorisedAction(
      CharityUser(UserType.Organisation, Some(id))
    )

  def individual(implicit ec: ExecutionContext) =
    new FakeClaimsAuthorisedAction(
      CharityUser(UserType.Individual, None)
    )
}
