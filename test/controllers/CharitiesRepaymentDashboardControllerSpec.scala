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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.twirl.api.Html
import util.ControllerSpecBase
import views.html.CharityRepaymentDashboardView

class CharitiesRepaymentDashboardControllerSpec extends ControllerSpecBase {

  "CharitiesRepaymentDashboardController onPageLoad" should {
    "return 200 OK and render the CharityRepaymentDashboardView view" in {
      val userId = Some("test-user-123")
      val mockView = mock[CharityRepaymentDashboardView]
      when(mockView.apply(eqTo(userId))(any(), any())).thenReturn(Html("<p>Success View</p>"))

      val controller = new CharitiesRepaymentDashboardController(cc, fakeAuth(), mockView)
      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) must include("Success View")
      verify(mockView).apply(eqTo(userId))(any(), any())
    }
  }
}
