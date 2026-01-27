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

package controllers

import controllers.HomeControllerSpec.Fixture
import controllers.actions.FakeClaimsAuthorisedAction
import models.requests.UserType
import models.requests.UserType.{Agent, Organisation}
import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import util.ControllerSpecBase

import scala.concurrent.ExecutionContext.Implicits.global

class HomeControllerSpec extends ControllerSpecBase {

  "HomeController landingPage" should {
    "redirect to CharitiesRepaymentDashboardController for Organisation users" in new Fixture {
      private val result = controller(Organisation).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardController.onPageLoad.url
    }

    "redirect to CharitiesRepaymentDashboardAgentController for Agent users" in new Fixture {
      private val result = controller(Agent).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardAgentController.onPageLoad.url
    }

    "redirect to AccessDeniedController for unsupported user types" in new Fixture {
      private val result = controller(UserType.Individual).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.AccessDeniedController.onPageLoad.url
    }
  }
}

object HomeControllerSpec {

  trait Fixture {
    private val cc = Helpers.stubMessagesControllerComponents()

    def controller(userType: UserType): HomeController =
      new HomeController(cc, new FakeClaimsAuthorisedAction(cc, userType, "test-user-123"))

    val request: FakeRequest[AnyContent] = FakeRequest(GET, "/")
  }
}
