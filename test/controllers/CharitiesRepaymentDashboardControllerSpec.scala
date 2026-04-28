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
import config.AppConfig
import connectors.ClaimsConnector
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import util.ControllerSpecBase
import views.html.{CharityRepaymentDashboardAgentView, CharityRepaymentDashboardView}

class CharitiesRepaymentDashboardControllerSpec extends ControllerSpecBase {

  "CharitiesRepaymentDashboardController onPageLoad" should {

    "return 200 OK and render the CharityRepaymentDashboardView view" in {
      val mockConfig: AppConfig                = mock[AppConfig]
      val mockClaimsConnector: ClaimsConnector = mock[ClaimsConnector]
      val mockRDSConnector: ClaimsConnector    = mock[ClaimsConnector]
      val orgId                                = "test-user-123"
      val mockOrgView                          = mock[CharityRepaymentDashboardView]
      val mockAgentView                        = mock[CharityRepaymentDashboardAgentView]

      (mockClaimsConnector
        .retrieveUnsubmittedClaims(using _: HeaderCarrier))
        .expects()
        .returning(Future.successful(GetClaimsResponse))

      (mockRDSConnector
        .getOrganisationName(orgId: String)(using _: HeaderCarrier))
        .expects(orgId, *)
        .returning(Future.successful(GetClaimsResponse))

      when(mockOrgView.apply(eqTo(Some(orgId)), any(), any(), any(), any(), any())(any(), any()))
        .thenReturn(Html("<p>Success View</p>"))

      val controller = new CharitiesRepaymentDashboardController(cc, fakeOrg(orgId), mockConfig, mockClaimsConnector, mockOrgView, mockAgentView)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) must include("Success View")
      verify(mockAgentView).apply(eqTo(Some(orgId)), any(), any(), any(), any(), any())(any(), any())
    }
  }
}
