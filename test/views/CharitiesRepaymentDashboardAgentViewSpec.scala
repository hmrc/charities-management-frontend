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

package views

import org.scalatestplus.play.*
import util.ViewSpec
import views.html.CharityRepaymentDashboardAgentView
import models.ClaimInfo
import services.PaginationService

class CharitiesRepaymentDashboardAgentViewSpec extends ViewSpec {

  private val view                                    = injectView[CharityRepaymentDashboardAgentView]
  private val makeRepaymentClaimUrl                   = "/make-repayment-claim"
  private val giftAidOtherIncomeCommunityBuildingsUrl = "/make-gift-aid-other-income-communit-buildings"
  private val hmrcServicesHomeUrl                     = "/hrmc-service-home"
  private val agentName                               = "Agent Name"
  private val claims = List(ClaimInfo(claimId = "123", hmrcCharitiesReference = Some("ABC123"), nameOfCharity = Some("Charity Name")))

  "CharityRepaymentDashboardAgentView" should {

    "render title and heading correctly" in {

      val paginationResult = PaginationService.paginateClaims(claims, 1, "/foo")
      val doc = asDocument(
        view(
          "ABC123",
          makeRepaymentClaimUrl,
          makeRepaymentClaimUrl + "?claimId=blank",
          "/delete-claim",
          agentName,
          giftAidOtherIncomeCommunityBuildingsUrl,
          hmrcServicesHomeUrl,
          paginationResult.paginationViewModel,
          paginationResult,
          claims
        )
      )

      assertTitle(doc, ViewUtils.titleWithPagination("charityRepaymentDashboardAgent.title", paginationResult))
      assertH1(doc, messages("charityRepaymentDashboardAgent.heading"))
    }
  }
}
