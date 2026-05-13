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

class GetOrganisationReferenceResponseSpec extends BaseSpec {

  "GetOrganisationReferenceResponse JSON format" - {

    "serialize with a Some organisation name" in {
      val response = GetOrganisationReferenceResponse(organisationName = Some("Test Org"))
      val json     = Json.toJson(response)
      (json \ "organisationName").asOpt[String] shouldBe Some("Test Org")
    }

    "serialize with None organisation name" in {
      val response = GetOrganisationReferenceResponse(organisationName = None)
      val json     = Json.toJson(response)
      (json \ "organisationName").asOpt[String] shouldBe None
    }

    "deserialize from JSON with organisation name" in {
      val json = Json.parse("""{"organisationName": "My Charity"}""")
      json.validate[GetOrganisationReferenceResponse] shouldBe JsSuccess(
        GetOrganisationReferenceResponse(Some("My Charity"))
      )
    }

    "deserialize from JSON without organisation name" in {
      val json = Json.parse("""{}""")
      json.validate[GetOrganisationReferenceResponse] shouldBe JsSuccess(
        GetOrganisationReferenceResponse(None)
      )
    }

    "round-trip serialize and deserialize" in {
      val response = GetOrganisationReferenceResponse(Some("Round Trip Org"))
      Json.toJson(response).as[GetOrganisationReferenceResponse] shouldBe response
    }
  }
}
