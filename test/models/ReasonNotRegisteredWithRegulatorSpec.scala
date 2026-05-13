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

package models

import play.api.libs.json.{JsError, JsString, Json}
import util.BaseSpec

class ReasonNotRegisteredWithRegulatorSpec extends BaseSpec {

  import ReasonNotRegisteredWithRegulator.given

  "ReasonNotRegisteredWithRegulator" - {

    "serialize each value to its string name" in {
      Json.toJson(ReasonNotRegisteredWithRegulator.LowIncome) shouldBe JsString("LowIncome")
      Json.toJson(ReasonNotRegisteredWithRegulator.Excepted) shouldBe JsString("Excepted")
      Json.toJson(ReasonNotRegisteredWithRegulator.Exempt) shouldBe JsString("Exempt")
      Json.toJson(ReasonNotRegisteredWithRegulator.Waiting) shouldBe JsString("Waiting")
    }

    "deserialize a valid string to the correct enum value" in {
      JsString("LowIncome").as[ReasonNotRegisteredWithRegulator] shouldBe ReasonNotRegisteredWithRegulator.LowIncome
      JsString("Excepted").as[ReasonNotRegisteredWithRegulator] shouldBe ReasonNotRegisteredWithRegulator.Excepted
      JsString("Exempt").as[ReasonNotRegisteredWithRegulator] shouldBe ReasonNotRegisteredWithRegulator.Exempt
      JsString("Waiting").as[ReasonNotRegisteredWithRegulator] shouldBe ReasonNotRegisteredWithRegulator.Waiting
    }

    "fail to deserialize an unknown string" in {
      val result = JsString("NotAValue").validate[ReasonNotRegisteredWithRegulator]
      result shouldBe a[JsError]
    }

    "fail to deserialize a non-string JSON value" in {
      val result = Json.parse("true").validate[ReasonNotRegisteredWithRegulator]
      result shouldBe a[JsError]
    }

    "round-trip serialize and deserialize all values" in {
      ReasonNotRegisteredWithRegulator.values.foreach { value =>
        Json.toJson(value).as[ReasonNotRegisteredWithRegulator] shouldBe value
      }
    }
  }
}
