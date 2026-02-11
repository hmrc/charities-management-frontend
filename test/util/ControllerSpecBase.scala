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

package util

import controllers.actions.*
import models.requests.UserType
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.*
import play.api.test.Helpers
import play.twirl.api.Html
import models.requests.CharityUser

import scala.concurrent.ExecutionContext

trait ControllerSpecBase extends PlaySpec with MockitoSugar with Results {

  protected val cc: MessagesControllerComponents = Helpers.stubMessagesControllerComponents()

  implicit protected val ec: ExecutionContext = cc.executionContext

  protected def fakeAuthorised(user: CharityUser): FakeClaimsAuthorisedAction =
    new FakeClaimsAuthorisedAction(user)

  protected def fakeAgent(id: String = "test-agent-id"): FakeClaimsAuthorisedAction =
    fakeAuthorised(CharityUser(UserType.Agent, Some(id)))

  protected def fakeOrg(id: String = "test-user-123"): FakeClaimsAuthorisedAction =
    fakeAuthorised(CharityUser(UserType.Organisation, Some(id)))

  protected def fakeIndividual: FakeClaimsAuthorisedAction =
    fakeAuthorised(CharityUser(UserType.Individual, None))

  protected def html(content: String): Html =
    Html(content)
}
