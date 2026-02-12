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

package controllers.actions

import config.AppConfig
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.~
import util.BaseSpec

import scala.concurrent.Future

class IdentifyClaimsAuthActionSpec extends BaseSpec {

  class Harness(action: IdentifyClaimsAuthAction) {
    def onPageLoad: Action[AnyContent] =
      action { request =>
        Results.Ok(
          s"UserType: ${request.charityUser.userType}, UserReferenceId: ${request.charityUser.referenceId.getOrElse("")}"
        )
      }
  }

  private val bodyParser = BodyParsers.Default(Helpers.stubPlayBodyParsers)
  private val mockConfig = mock[AppConfig]

  when(mockConfig.loginUrl).thenReturn("/login")
  when(mockConfig.loginContinueUrl).thenReturn("/continue")

  "IdentifyClaimsUserAction" should {

    "identify Agent user" in {
      val mockAuthConnector = mock[AuthConnector]
      val enrolments =
        Enrolments(Set(Enrolment("HMRC-CHAR-AGENT", Seq(EnrolmentIdentifier("AGENTCHARID", "A123")), "Activated")))

      when(
        mockAuthConnector.authorise(any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(Some(AffinityGroup.Agent), enrolments)))

      val action     = new IdentifyClaimsAuthAction(mockAuthConnector, mockConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Agent, UserReferenceId: A123"
    }

    "identify Organisation user" in {
      val mockAuthConnector = mock[AuthConnector]
      val enrolments =
        Enrolments(Set(Enrolment("HMRC-CHAR-ORG", Seq(EnrolmentIdentifier("CHARID", "O123")), "Activated")))

      when(
        mockAuthConnector.authorise(any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(Some(AffinityGroup.Organisation), enrolments)))

      val action     = new IdentifyClaimsAuthAction(mockAuthConnector, mockConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Organisation, UserReferenceId: O123"
    }

    "identify Individual user" in {
      val mockAuthConnector = mock[AuthConnector]

      when(
        mockAuthConnector.authorise(any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(Some(AffinityGroup.Individual), Enrolments(Set.empty))))

      val action     = new IdentifyClaimsAuthAction(mockAuthConnector, mockConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Individual, UserReferenceId: "
    }
  }
}
