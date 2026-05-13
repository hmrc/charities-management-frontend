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

package uk.gov.hmrc.charitiesmanagementfrontend.connectors

import connectors.ClaimsConnector
import models.*
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status.OK
import uk.gov.hmrc.http.HeaderCarrier
import utils.{ComponentSpecHelper, WiremockMethods}

class ClaimsConnectorISpec extends ComponentSpecHelper with WiremockMethods {

  private val connector = app.injector.instanceOf[ClaimsConnector]
  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  given HeaderCarrier = HeaderCarrier()

  private val claimId           = "123"
  private val charityReference  = "AB12345"
  private val agentReference    = "AGENT123"

  "retrieveUnsubmittedClaims" should {

    "return claims when backend returns 200" in {

      val response =
        GetClaimsResponse(
          claimsCount = 1,
          claimsList = List(
            ClaimInfo(claimId, Some("1234567890"), Some("Test charity"))
          )
        )

      when(GET, "/charities-claims/claims\\?claimSubmitted=false")
        .thenReturn(OK, response)

      val result = connector.retrieveUnsubmittedClaims.futureValue

      result.claimsCount shouldBe 1
    }
  }

  "getOrganisationName" should {

    "return organisation details when backend returns 200" in {

      val response =
        GetOrganisationReferenceResponse(
          organisationName = Some("Test Charity")
        )

      when(GET, s"/charities-claims/charities/organisations/$charityReference")
        .thenReturn(OK, response)

      val result = connector.getOrganisationName(charityReference).futureValue

      result.organisationName shouldBe Some("Test Charity")
    }
  }

  "getAgentName" should {

    "return agent details when backend returns 200" in {

      val response =
        GetAgentReferenceResponse(
          agentName = "Test Agent"
        )

      when(GET, s"/charities-claims/charities/agents/$agentReference")
        .thenReturn(OK, response)

      val result = connector.getAgentName(agentReference).futureValue

      result.agentName shouldBe "Test Agent"
    }
  }
}
