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

package uk.gov.hmrc.charitiesmanagementfrontend

import models.requests.UserType
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.charitiesmanagementfrontend.helpers.IntegrationTestSupport

class StartControllerISpec
  extends AnyWordSpec
    with Matchers
    with IntegrationTestSupport {

  private val startUrl = "/charities-management/start"
  private val keepAliveUrl = "/charities-management/keep-alive"
  private val timedOutUrl = "/charities-management/timed-out"

  "GET /start" should {
    "redirect to HomeController" in {
      val app = appWithUser(UserType.Organisation)

      val request = FakeRequest(GET, startUrl)

      val result = route(app, request).value

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe
        controllers.routes.HomeController.landingPage.url
    }
  }

  "GET /keep-alive" should {
    "return 200 when user is authorised" in {
      val app = appWithUser(UserType.Organisation)

      val request = FakeRequest(GET, keepAliveUrl)

      val result = route(app, request).value

      status(result) shouldBe OK
    }

    "return 200 for Agent user as well" in {
      val app = appWithUser(UserType.Agent)

      val request = FakeRequest(GET, keepAliveUrl)

      val result = route(app, request).value

      status(result) shouldBe OK
    }
  }

  "GET /timed-out" should {
    "redirect to start page" in {
      val app = appWithUser(UserType.Organisation)

      val request = FakeRequest(GET, timedOutUrl)

      val result = route(app, request).value

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe
        controllers.routes.StartController.start.url
    }
  }
}