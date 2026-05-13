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

package models.requests

import util.BaseSpec

class CharityUserSpec extends BaseSpec {

  "CharityUser" - {

    "create an Organisation user with a reference ID" in {
      val user = CharityUser(UserType.Organisation, Some("org-ref-123"))
      user.userType shouldBe UserType.Organisation
      user.referenceId shouldBe Some("org-ref-123")
    }

    "create an Agent user with a reference ID" in {
      val user = CharityUser(UserType.Agent, Some("agent-ref-456"))
      user.userType shouldBe UserType.Agent
      user.referenceId shouldBe Some("agent-ref-456")
    }

    "create an Individual user with no reference ID" in {
      val user = CharityUser(UserType.Individual, None)
      user.userType shouldBe UserType.Individual
      user.referenceId shouldBe None
    }

    "support equality comparison" in {
      val user1 = CharityUser(UserType.Organisation, Some("ref"))
      val user2 = CharityUser(UserType.Organisation, Some("ref"))
      val user3 = CharityUser(UserType.Agent, Some("ref"))
      user1 shouldBe user2
      user1 should not be user3
    }
  }
}
