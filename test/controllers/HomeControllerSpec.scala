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

class HomeControllerSpec extends ControllerSpecBase {

  "HomeController landingPage" should {

    "redirect to CharitiesRepaymentDashboardController for Organisation users" in {
      val result = controllerAsOrg.landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardController.onPageLoad.url
    }

    "redirect to CharitiesRepaymentDashboardAgentController for Agent users" in {
      val result = controllerAsAgent.landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.CharitiesRepaymentDashboardAgentController.onPageLoad.url
    }

    "redirect to AccessDeniedController for unsupported user types i.e Individual" in {
      val result = controllerAsIndividual.landingPage(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.AccessDeniedController.onPageLoad.url
    }
  }

  private val request: FakeRequest[AnyContent] =
    FakeRequest(GET, "/")

  private def controllerAsOrg: HomeController =
    new HomeController(cc, fakeOrg())

  private def controllerAsAgent: HomeController =
    new HomeController(cc, fakeAgent())

  private def controllerAsIndividual: HomeController =
    new HomeController(cc, fakeIndividual)
}
