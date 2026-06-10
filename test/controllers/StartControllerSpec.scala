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

import org.scalatestplus.play.PlaySpec
import play.api.mvc.AnyContent
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import util.ControllerSpecBase
import views.html.TimedOutView
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.any
import play.twirl.api.Html

class StartControllerSpec extends ControllerSpecBase {

  val request: FakeRequest[AnyContent] = FakeRequest(GET, "/")

  val timedOutView: TimedOutView = mock[TimedOutView]

  when(timedOutView.apply()(any(), any())).thenReturn(Html("<p>Timed Out</p>"))

  val controller: StartController = new StartController(cc, fakeOrg(), timedOutView)

  "StartController" should {

    "redirect to landing page when start is called" in {
      val result = controller.start(request)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe
        controllers.routes.HomeController.landingPage.url
    }

    "return OK when keepAlive is called and user is authorised" in {
      val result = controller.keepAlive(request)

      status(result) mustBe OK
    }

    "return OK and render the timed out view when timedOut is called" in {
      val result = controller.timedOut(request)

      status(result) mustBe OK
      contentAsString(result) must include("Timed Out")
    }
  }
}
