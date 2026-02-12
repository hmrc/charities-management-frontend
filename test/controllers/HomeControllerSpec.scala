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

package controllers

import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import util.ControllerSpecBase
import models.requests.UserType

class HomeControllerSpec extends ControllerSpecBase {

  private val request: FakeRequest[AnyContent] =
    FakeRequest(GET, "/")

  "HomeController landingPage" should {

    "redirect Organisation users to the organisation dashboard" in {
      val result = controller(UserType.Organisation).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardController.onPageLoad.url
    }

    "redirect Agent users to the agent dashboard" in {
      val result = controller(UserType.Agent).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardAgentController.onPageLoad.url
    }

    "redirect Individual users to access denied" in {
      val result = controller(UserType.Individual).landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.AccessDeniedController.onPageLoad.url
    }
  }

  // --------------------------------------------------------------------------

  private def controller(userType: UserType): HomeController =
    new HomeController(
      cc,
      authorisedAction(userType)
    )

  private def authorisedAction(userType: UserType) =
    userType match {
      case UserType.Agent        => fakeAgent()
      case UserType.Organisation => fakeOrg()
      case UserType.Individual   => fakeIndividual
    }
}
