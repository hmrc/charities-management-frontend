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

import play.api.libs.json.{JsSuccess, Json}
import util.BaseSpec

class GetClaimsResponseSpec extends BaseSpec {

  "GetClaimsResponse JSON format" - {

    "serialize with an empty claims list" in {
      val response = GetClaimsResponse(claimsCount = 0, claimsList = List.empty)
      val json     = Json.toJson(response)
      (json \ "claimsCount").as[Int] shouldBe 0
      (json \ "claimsList").as[List[ClaimInfo]] shouldBe List.empty
    }

    "serialize with multiple claims" in {
      val claims   = List(ClaimInfo("id-1"), ClaimInfo("id-2", Some("ref")))
      val response = GetClaimsResponse(claimsCount = 2, claimsList = claims)
      val json     = Json.toJson(response)
      (json \ "claimsCount").as[Int] shouldBe 2
      (json \ "claimsList").as[List[ClaimInfo]] shouldBe claims
    }

    "deserialize a JSON object to GetClaimsResponse" in {
      val json = Json.parse(
        """{
          |  "claimsCount": 1,
          |  "claimsList": [{"claimId": "id-1"}]
          |}""".stripMargin
      )
      json.validate[GetClaimsResponse] shouldBe JsSuccess(
        GetClaimsResponse(claimsCount = 1, claimsList = List(ClaimInfo("id-1")))
      )
    }

    "round-trip serialize and deserialize" in {
      val response = GetClaimsResponse(
        claimsCount = 2,
        claimsList = List(ClaimInfo("a"), ClaimInfo("b", Some("ref-b")))
      )
      Json.toJson(response).as[GetClaimsResponse] shouldBe response
    }
  }
}
