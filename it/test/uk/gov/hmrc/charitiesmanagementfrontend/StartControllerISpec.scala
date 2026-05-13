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

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers.*
import uk.gov.hmrc.charitiesmanagementfrontend.stubs.AuthStub
import utils.ComponentSpecHelper

class StartControllerISpec
  extends ComponentSpecHelper with AuthStub {

  private val startUrl = "/start"
  private val keepAliveUrl = "/keep-alive"
  private val timedOutUrl = "/timed-out"

  "GET /start" should {
    "redirect to HomeController" in {
      stubAuthRequest()

      val result = get(startUrl)
      result.status shouldBe SEE_OTHER
      result.header(LOCATION).value shouldBe
        controllers.routes.HomeController.landingPage.url
    }
  }

  "GET /keep-alive" should {
    "return 200 when user is authorised" in {
      stubAuthRequest()

      val result = get(keepAliveUrl)
      result.status shouldBe OK
    }

    "return 200 for Agent user as well" in {
      stubAgentAuthRequest()

      val result = get(keepAliveUrl)
      result.status shouldBe OK
    }
  }

  "GET /timed-out" should {
    "redirect to start page" in {
      stubAuthRequest()

      val result = get(timedOutUrl)

      result.status shouldBe SEE_OTHER
      result.header(LOCATION).value shouldBe
        controllers.routes.StartController.start.url
    }
  }
}