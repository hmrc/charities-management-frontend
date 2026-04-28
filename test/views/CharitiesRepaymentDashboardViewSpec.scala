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
import views.html.CharityRepaymentDashboardView

class CharitiesRepaymentDashboardViewSpec extends ViewSpec {

  private val view                                    = injectView[CharityRepaymentDashboardView]
  private val makeRepaymentClaimUrl                   = "/make-repayment-claim"
  private val giftAidOtherIncomeCommunityBuildingsUrl = "/make-gift-aid-other-income-community-buildings"
  private val hmrcServicesHomeUrl                     = "/hrmc-service-home"
  private val claimExistTrue: Boolean                 = true
  private val claimExistFalse: Boolean                = false
  private val orgName                                 = Some("Some Org Name")
  private val ref                                     = "ABC123"

  "CharityRepaymentDashboardView" should {

    "render title and heading correctly without reference" in {
      val doc =
        asDocument(view(ref, makeRepaymentClaimUrl, orgName, giftAidOtherIncomeCommunityBuildingsUrl, hmrcServicesHomeUrl, claimExistTrue))

      assertTitle(doc, messages("charityRepaymentDashboard.title"))
      assertH1(doc, messages("charityRepaymentDashboard.heading"))
      assertH3(doc, messages("charityRepaymentDashboard.subheading"))
      // assertPara(doc, messages("charityRepaymentDashboard.para"))
    }
  }
}
