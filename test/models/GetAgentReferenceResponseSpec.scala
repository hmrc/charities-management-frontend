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

import play.api.libs.json.{JsError, JsSuccess, Json}
import util.BaseSpec

class GetAgentReferenceResponseSpec extends BaseSpec {

  "GetAgentReferenceResponse JSON format" - {

    "serialize to JSON" in {
      val response = GetAgentReferenceResponse(agentName = "Agent Smith")
      val json     = Json.toJson(response)
      (json \ "agentName").as[String] shouldBe "Agent Smith"
    }

    "deserialize from JSON" in {
      val json = Json.parse("""{"agentName": "Agent Jones"}""")
      json.validate[GetAgentReferenceResponse] shouldBe JsSuccess(
        GetAgentReferenceResponse("Agent Jones")
      )
    }

    "fail to deserialize when agentName is missing" in {
      val json   = Json.parse("""{}""")
      val result = json.validate[GetAgentReferenceResponse]
      result shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      val response = GetAgentReferenceResponse("Round Trip Agent")
      Json.toJson(response).as[GetAgentReferenceResponse] shouldBe response
    }
  }
}
