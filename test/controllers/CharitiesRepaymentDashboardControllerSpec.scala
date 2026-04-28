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

package controllers
import play.api.test.FakeRequest
import util.ControllerSpecBase
import config.AppConfig
import views.html.{CharityRepaymentDashboardAgentView, CharityRepaymentDashboardView}
import models.{GetClaimsResponse, GetOrganisationReferenceResponse}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import play.api.test.Helpers.*
import org.mockito.Mockito.*
import play.twirl.api.Html
import connectors.ClaimsConnector

import scala.concurrent.Future

class CharitiesRepaymentDashboardControllerSpec extends ControllerSpecBase {

  "CharitiesRepaymentDashboardController onPageLoad" should {

    "return 200 OK and render the CharityRepaymentDashboardView view" in {
      val mockConfig: AppConfig                = mock[AppConfig]
      val mockClaimsConnector: ClaimsConnector = mock[ClaimsConnector]
      val orgId                                = "test-user-123"
      val mockOrgView                          = mock[CharityRepaymentDashboardView]

      when(mockClaimsConnector.retrieveUnsubmittedClaims(using any()))
        .thenReturn(Future.successful(GetClaimsResponse(claimsList = List.empty, claimsCount = 0)))

      when(mockClaimsConnector.getOrganisationName(any())(using any()))
        .thenReturn(Future.successful(GetOrganisationReferenceResponse(Some("Test Org"))))

      when(mockOrgView.apply(eqTo(orgId), any(), any(), any(), any(), any())(any(), any()))
        .thenReturn(Html("<p>Success View</p>"))

      val controller = new CharitiesRepaymentDashboardController(cc, fakeOrg(orgId), mockConfig, mockClaimsConnector, mockOrgView, null)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) must include("Success View")
      verify(mockOrgView).apply(eqTo(orgId), any(), any(), any(), any(), any())(any(), any())
    }
  }
}
