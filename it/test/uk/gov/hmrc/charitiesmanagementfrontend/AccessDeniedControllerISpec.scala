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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.charitiesmanagementfrontend.helpers.IntegrationTestSupport

class AccessDeniedControllerISpec
  extends AnyWordSpec
    with Matchers
    with IntegrationTestSupport {

  private val url = "/charities-management/there-is-a-problem-access-denied"

  "GET /there-is-a-problem-access-denied" should {

    "return 403 Forbidden" in {
      val app = appWithUser(models.requests.UserType.Organisation)

      val request = FakeRequest(GET, url)

      val result = route(app, request).value
      status(result) shouldBe FORBIDDEN
    }

    "render the error page" in {
      val app = appWithUser(models.requests.UserType.Organisation)

      val request = FakeRequest(GET, url)

      val result = route(app, request).value
      val body = contentAsString(result)
      body should include("there is a problem")
    }
  }
}