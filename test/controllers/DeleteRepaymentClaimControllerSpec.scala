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

///*
// * Copyright 2026 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package controllers
//
//import models.GetAgentReferenceResponse
//import org.scalatest.BeforeAndAfterEach
//import org.scalatest.matchers.should.Matchers
//import org.scalatestplus.mockito.MockitoSugar
//import play.twirl.api.HtmlFormat
//import play.api.test.FakeRequest
//import util.ControllerSpecBase
//import config.AppConfig
//import views.html.DeleteRepaymentClaimView
//import org.mockito.ArgumentMatchers.{any, eq as eqTo}
//import play.api.test.Helpers.*
//import org.mockito.Mockito.*
//import play.twirl.api.Html
//import connectors.ClaimsConnector
//import scala.concurrent.Future
//import forms.YesNoFormProvider
//import org.scalatest.matchers.should.Matchers.shouldBe
//
//class DeleteRepaymentClaimControllerSpec extends ControllerSpecBase with MockitoSugar with BeforeAndAfterEach {
//
//  val mockClaimsConnector: ClaimsConnector = mock[ClaimsConnector]
//  val mockView: DeleteRepaymentClaimView   = mock[DeleteRepaymentClaimView]
//  val mockConfig: AppConfig                = mock[AppConfig]
//
//  val formProvider = new YesNoFormProvider()
//
//  val claimId     = "CLAIM-001"
//  val referenceId = "REF-123"
//  val agentName   = "Test Agent Ltd"
//  val agentId     = "agent123"
//
//  val onPageLoadRoute: String = routes.DeleteRepaymentClaimController.onPageLoad(claimId).url
//  val onSubmitRoute: String   = routes.DeleteRepaymentClaimController.onSubmit(claimId).url
//
//  private def stubViewToReturnHtml(): Unit =
//    when(mockView.apply(any(), any(), any())(any(), any()))
//      .thenReturn(HtmlFormat.empty)
//
//  def controller: DeleteRepaymentClaimController =
//    new DeleteRepaymentClaimController(
//      controllerComponents = stubMessagesControllerComponents(),
//      authorisedAction = fakeAgent(agentId),
//      config = mockConfig,
//      claimsConnector = mockClaimsConnector,
//      view = mockView,
//      formProvider = formProvider
//    )
//
//  override def beforeEach(): Unit = {
//    super.beforeEach()
//    reset(mockClaimsConnector, mockView, fakeAgent(agentId))
//    stubViewToReturnHtml()
//  }
//
//  "DeleteRepaymentClaimController onPageLoad" should {
//
//    "return 200 OK and render the view when a referenceId is present" in {
//      val fakeReq = FakeRequest(GET, onPageLoadRoute)
//        .withFormUrlEncodedBody()
//
//      when(mockClaimsConnector.getAgentName(eqTo(referenceId))(using any()))
//        .thenReturn(Future.successful(GetAgentReferenceResponse(agentName)))
//
//      val result = controller.onPageLoad(claimId)(fakeReq)
//
//      status(result) shouldBe OK
//      verify(mockView).apply(any(), eqTo(referenceId), eqTo(agentName))(any(), any())
//    }
//
//    "redirect to AccessDeniedController when referenceId is absent" in {
//      val fakeReq = FakeRequest(GET, onPageLoadRoute)
//        .withFormUrlEncodedBody()
//
//      val result = controller.onPageLoad(claimId)(fakeReq)
//
//      status(result) shouldBe SEE_OTHER
//      redirectLocation(result) shouldBe Some(routes.AccessDeniedController.onPageLoad.url)
//
//      verifyNoInteractions(mockClaimsConnector)
//    }
//
//  }
//
//  "DeleteRepaymentClaimController onSubmit" should {
//
//    "redirect to AccessDeniedController when referenceId is absent" in {
//      val fakeReq = FakeRequest(POST, onSubmitRoute)
//        .withFormUrlEncodedBody("value" -> "true")
//
//      val result = controller.onSubmit(claimId)(fakeReq)
//
//      status(result) shouldBe SEE_OTHER
//      redirectLocation(result) shouldBe Some(routes.AccessDeniedController.onPageLoad.url)
//
//      verifyNoInteractions(mockClaimsConnector)
//    }
//
//    "return 400 BadRequest and re-render the view when the form is invalid" in {
//      val fakeReq = FakeRequest(POST, onSubmitRoute)
//        .withFormUrlEncodedBody()
//
//      when(mockClaimsConnector.getAgentName(eqTo(referenceId))(using any()))
//        .thenReturn(Future.successful(GetAgentReferenceResponse(agentName)))
//
//      val result = controller.onSubmit(claimId)(fakeReq)
//
//      status(result) shouldBe BAD_REQUEST
//
//      verify(mockView).apply(any(), eqTo(claimId), eqTo(agentName))(any(), any())
//
//      verify(mockClaimsConnector, never()).deleteClaim(any())(using any())
//    }
//
//    "redirect to CharitiesRepaymentDashboard when user confirms deletion (Yes)" in {
//      val fakeReq = FakeRequest(POST, onSubmitRoute)
//        .withFormUrlEncodedBody("value" -> "true")
//
//      when(mockClaimsConnector.deleteClaim(eqTo(claimId))(using any()))
//        .thenReturn(Future.successful(true))
//
//      val result = controller.onSubmit(claimId)(fakeReq)
//
//      status(result) shouldBe SEE_OTHER
//      redirectLocation(result) shouldBe
//        Some(routes.CharitiesRepaymentDashboardController.onPageLoad.url)
//
//      verify(mockClaimsConnector).deleteClaim(eqTo(claimId))(using any())
//    }
//
//    "fail with a RuntimeException when user declines deletion (No)" in {
//      val fakeReq = FakeRequest(POST, onSubmitRoute)
//        .withFormUrlEncodedBody("value" -> "false")
//
//      when(mockClaimsConnector.deleteClaim(eqTo(claimId))(using any()))
//        .thenReturn(Future.successful(false))
//
//      val result = controller.onSubmit(claimId)(fakeReq)
//
//      status(result) shouldBe SEE_OTHER
//      redirectLocation(result) shouldBe
//        Some(routes.CharitiesRepaymentDashboardController.onPageLoad.url)
//
//      verify(mockClaimsConnector).deleteClaim(eqTo(claimId))(using any())
//    }
//
//  }
//
//}
