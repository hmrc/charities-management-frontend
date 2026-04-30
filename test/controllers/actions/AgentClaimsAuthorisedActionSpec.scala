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
import org.scalatest.OptionValues.*
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.~

import uk.gov.hmrc.http.HeaderCarrier
import util.{BaseSpec, FakeAuthConnector}

import scala.concurrent.{ExecutionContext, Future}

class AgentClaimsAuthorisedActionSpec extends BaseSpec {

  given ExecutionContext = ExecutionContext.global

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val bodyParser = BodyParsers.Default(Helpers.stubPlayBodyParsers)

  private lazy val application: Application = new GuiceApplicationBuilder().build()

  private lazy val appConfig: AppConfig = application.injector.instanceOf[AppConfig]

  class Harness(action: AgentClaimsAuthorisedAction) {
    def onPageLoad: Action[AnyContent] =
      action { request =>
        Results.Ok(
          s"UserType: ${request.charityUser.userType}, UserReferenceId: ${request.charityUser.referenceId.getOrElse("")}"
        )
      }
  }

  "AgentClaimsAuthorisedAction" - {

    "allow Agent with valid enrolment" in {

      val enrolments =
        Enrolments(
          Set(
            Enrolment(
              "HMRC-CHAR-AGENT",
              Seq(EnrolmentIdentifier("AGENTCHARID", "A123")),
              "Activated"
            )
          )
        )

      val fakeAuthConnector =
        new FakeAuthConnector(
          Future.successful(new ~(Some(AffinityGroup.Agent), enrolments))
        )
      val action     = new AgentClaimsAuthorisedAction(fakeAuthConnector, appConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) shouldBe OK
      contentAsString(result) shouldBe "UserType: Agent, UserReferenceId: A123"
    }

    "deny Agent without enrolment" in {

      val fakeAuthConnector =
        new FakeAuthConnector(
          Future.successful(new ~(Some(AffinityGroup.Agent), Enrolments(Set.empty)))
        )

      val action     = new AgentClaimsAuthorisedAction(fakeAuthConnector, appConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.routes.AccessDeniedController.onPageLoad.url
      )
    }

    "deny Organisation user" in {
      val enrolments =
        Enrolments(
          Set(
            Enrolment(
              "HMRC-CHAR-ORG",
              Seq(EnrolmentIdentifier("CHARID", "O123")),
              "Activated"
            )
          )
        )

      val fakeAuthConnector =
        new FakeAuthConnector(
          Future.successful(new ~(Some(AffinityGroup.Organisation), enrolments))
        )

      val action     = new AgentClaimsAuthorisedAction(fakeAuthConnector, appConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.routes.AccessDeniedController.onPageLoad.url
      )
    }

    "redirect to login when AuthorisationException is thrown" in {
      val fakeAuthConnector =
        new FakeAuthConnector(
          Future.failed(new MissingBearerToken())
        )

      val action     = new AgentClaimsAuthorisedAction(fakeAuthConnector, appConfig, bodyParser)
      val controller = new Harness(action)

      val result = controller.onPageLoad(FakeRequest())

      status(result) shouldBe SEE_OTHER
      redirectLocation(result).value should include(appConfig.loginUrl)
      redirectLocation(result).value should include("continue")
    }
  }
}
