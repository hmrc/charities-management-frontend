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

class UserTypeSpec extends BaseSpec {

  "UserType" - {

    "have three values" in {
      UserType.values.toSet shouldBe Set(UserType.Organisation, UserType.Agent, UserType.Individual)
    }

    "distinguish Organisation from Agent and Individual" in {
      val userType = UserType.Organisation
      userType shouldBe UserType.Organisation
      userType should not be UserType.Agent
      userType should not be UserType.Individual
    }

    "distinguish Agent from Organisation and Individual" in {
      val userType = UserType.Agent
      userType shouldBe UserType.Agent
      userType should not be UserType.Organisation
      userType should not be UserType.Individual
    }

    "distinguish Individual from Organisation and Agent" in {
      val userType = UserType.Individual
      userType shouldBe UserType.Individual
      userType should not be UserType.Organisation
      userType should not be UserType.Agent
    }
  }
}
