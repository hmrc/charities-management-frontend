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

class CharitiesRepaymentDashboardControllerISpec extends AnyWordSpec with Matchers with IntegrationTestSupport {

  private val url = "/charities-management/charity-repayment-dashboard"

  "GET /charity-repayment-dashboard" should {

//    "return 200 for Organisation user" in {
//      val app = appWithUser(UserType.Organisation)
//
//      val request = FakeRequest(GET, url)
//
//      val result = route(app, request).value
//      status(result) shouldBe OK
//    }

    // "redirect Agent user to access denied" in {
    //   val app = appWithUser(UserType.Agent)

    //   val request = FakeRequest(GET, url)

    //   val result = route(app, request).value

    //   status(result) shouldBe SEE_OTHER
    //   redirectLocation(result).value shouldBe
    //     controllers.routes.AccessDeniedController.onPageLoad.url
    // }

    "redirect Individual user to access denied" in {
      val app = appWithUser(UserType.Individual)

      val request = FakeRequest(GET, url)

      val result = route(app, request).value

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value shouldBe
        controllers.routes.AccessDeniedController.onPageLoad.url
    }
  }
}
