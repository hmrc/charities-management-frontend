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

class ClaimInfoSpec extends BaseSpec {

  "ClaimInfo JSON format" - {

    "serialize a ClaimInfo with all fields" in {
      val claimInfo = ClaimInfo(
        claimId = "abc-123",
        hmrcCharitiesReference = Some("ref-456"),
        nameOfCharity = Some("Test Charity"),
        lastVisitedAt = Some(1700000000000L)
      )
      val json = Json.toJson(claimInfo)
      (json \ "claimId").as[String] shouldBe "abc-123"
      (json \ "hmrcCharitiesReference").as[String] shouldBe "ref-456"
      (json \ "nameOfCharity").as[String] shouldBe "Test Charity"
      (json \ "lastVisitedAt").as[Long] shouldBe 1700000000000L
    }

    "serialize a ClaimInfo with only required fields" in {
      val claimInfo = ClaimInfo(claimId = "min-id")
      val json      = Json.toJson(claimInfo)
      (json \ "claimId").as[String] shouldBe "min-id"
      (json \ "hmrcCharitiesReference").asOpt[String] shouldBe None
      (json \ "nameOfCharity").asOpt[String] shouldBe None
      (json \ "lastVisitedAt").asOpt[Long] shouldBe None
    }

    "deserialize a full JSON object to ClaimInfo" in {
      val json = Json.parse(
        """{
          |  "claimId": "abc-123",
          |  "hmrcCharitiesReference": "ref-456",
          |  "nameOfCharity": "Test Charity",
          |  "lastVisitedAt": 1700000000000
          |}""".stripMargin
      )
      json.validate[ClaimInfo] shouldBe JsSuccess(
        ClaimInfo(
          claimId = "abc-123",
          hmrcCharitiesReference = Some("ref-456"),
          nameOfCharity = Some("Test Charity"),
          lastVisitedAt = Some(1700000000000L)
        )
      )
    }

    "deserialize a minimal JSON object to ClaimInfo" in {
      val json = Json.parse("""{"claimId": "min-id"}""")
      json.validate[ClaimInfo] shouldBe JsSuccess(ClaimInfo(claimId = "min-id"))
    }

    "round-trip serialize and deserialize" in {
      val claimInfo = ClaimInfo(
        claimId = "rt-123",
        hmrcCharitiesReference = Some("ref-rt"),
        nameOfCharity = Some("RT Charity"),
        lastVisitedAt = Some(999999L)
      )
      Json.toJson(claimInfo).as[ClaimInfo] shouldBe claimInfo
    }
  }
}
