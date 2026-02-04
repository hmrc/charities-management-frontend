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
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import play.twirl.api.Html
import util.ControllerSpecBase
import views.html.ErrorView

class AccessDeniedControllerSpec extends ControllerSpecBase {

  "AccessDeniedController onPageLoad" should {

    "return 403 Forbidden and render the error view" in {
      val accountUrl = "http://example.com/account"

      val mockConfig: AppConfig = mock[AppConfig]
      val mockView: ErrorView   = mock[ErrorView]

      when(mockConfig.accountUrl).thenReturn(accountUrl)
      when(mockView.apply(eqTo(accountUrl))(any(), any())).thenReturn(Html("<p>Access denied</p>"))

      val controller = new AccessDeniedController(cc, mockConfig, mockView)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe FORBIDDEN
      contentAsString(result) must include("Access denied")
      verify(mockView).apply(eqTo("http://example.com/account"))(any(), any())
    }
  }
}
