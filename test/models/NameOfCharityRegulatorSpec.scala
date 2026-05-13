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

class NameOfCharityRegulatorSpec extends BaseSpec {

  import NameOfCharityRegulator.given

  "NameOfCharityRegulator" - {

    "serialize each value to its string name" in {
      Json.toJson(NameOfCharityRegulator.EnglandAndWales) shouldBe JsString("EnglandAndWales")
      Json.toJson(NameOfCharityRegulator.NorthernIreland) shouldBe JsString("NorthernIreland")
      Json.toJson(NameOfCharityRegulator.Scottish) shouldBe JsString("Scottish")
      Json.toJson(NameOfCharityRegulator.None) shouldBe JsString("None")
    }

    "deserialize a valid string to the correct enum value" in {
      JsString("EnglandAndWales").as[NameOfCharityRegulator] shouldBe NameOfCharityRegulator.EnglandAndWales
      JsString("NorthernIreland").as[NameOfCharityRegulator] shouldBe NameOfCharityRegulator.NorthernIreland
      JsString("Scottish").as[NameOfCharityRegulator] shouldBe NameOfCharityRegulator.Scottish
      JsString("None").as[NameOfCharityRegulator] shouldBe NameOfCharityRegulator.None
    }

    "fail to deserialize an unknown string" in {
      val result = JsString("Unknown").validate[NameOfCharityRegulator]
      result shouldBe a[JsError]
    }

    "fail to deserialize a non-string JSON value" in {
      val result = Json.parse("42").validate[NameOfCharityRegulator]
      result shouldBe a[JsError]
    }

    "round-trip serialize and deserialize all values" in {
      NameOfCharityRegulator.values.foreach { value =>
        Json.toJson(value).as[NameOfCharityRegulator] shouldBe value
      }
    }
  }
}
