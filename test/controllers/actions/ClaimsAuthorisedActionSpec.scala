/*
 * Copyright 2025 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.mvc.*
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import util.BaseSpec

import scala.concurrent.{ExecutionContext, Future}

class ClaimsAuthorisedActionSpec extends BaseSpec {

  class Harness(authorisedAction: ClaimsAuthorisedAction) {
    def onPageLoad: Action[AnyContent] = authorisedAction { request =>
      Results.Ok(s"UserType: ${request.charUser.userType}, UserReferenceId: ${request.charUser.charUserId.getOrElse("")}")
    }
  }

  val bodyParser: BodyParsers.Default = BodyParsers.Default(Helpers.stubPlayBodyParsers)

  "AuthorisedAction" should {
    "create AuthorisedRequest when user has an Organisation affinity group" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]

      when(
        mockAuthConnector.authorise(
          any[Predicate],
          any[Retrieval[Option[AffinityGroup] ~ Enrolments]]
        )(any[HeaderCarrier], any[ExecutionContext])
      ).thenReturn(
        Future.successful(
          new~(
            Some(AffinityGroup.Organisation),
            Enrolments(
              Set(
                Enrolment(
                  "HMRC-CHAR-ORG",
                  Seq(EnrolmentIdentifier("CHARID", "1234567890")),
                  "Activated"
                )
              )
            )
          )
        )
      )
      val authorisedAction =
        new DefaultClaimsAuthorisedAction(mockAuthConnector, bodyParser)

      val controller = new Harness(authorisedAction)
      val result = controller.onPageLoad(FakeRequest("GET", "/test"))
      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Organisation, UserReferenceId: 1234567890"
    }

    "create AuthorisedRequest when user has an Agent affinity group" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]

      when(
        mockAuthConnector.authorise(
          any[Predicate],
          any[Retrieval[Option[AffinityGroup] ~ Enrolments]]
        )(any[HeaderCarrier], any[ExecutionContext])
      ).thenReturn(
        Future.successful(
          new~(
            Some(AffinityGroup.Agent),
            Enrolments(
              Set(
                Enrolment(
                  "HMRC-CHAR-AGENT",
                  Seq(EnrolmentIdentifier("AGENTCHARID", "1234567890")),
                  "Activated"
                )
              )
            )
          )
        )
      )

      val authorisedAction =
        new DefaultClaimsAuthorisedAction(mockAuthConnector, bodyParser)

      val controller = new Harness(authorisedAction)
      val result = controller.onPageLoad(FakeRequest("GET", "/test"))
      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Agent, UserReferenceId: 1234567890"
    }

    "create AuthorisedRequest with no User Reference when user has an Individual affinity group" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]

      when(
        mockAuthConnector.authorise(
          any[Predicate],
          any[Retrieval[Option[AffinityGroup] ~ Enrolments]]
        )(any[HeaderCarrier], any[ExecutionContext])
      ).thenReturn(
        Future.successful(
          new~(
            Some(AffinityGroup.Individual),
            Enrolments(
              Set.empty
            )
          )
        )
      )

      val authorisedAction =
        new DefaultClaimsAuthorisedAction(mockAuthConnector, bodyParser)

      val controller = new Harness(authorisedAction)
      val result = controller.onPageLoad(FakeRequest("GET", "/test"))
      status(result) mustBe OK
      contentAsString(result) mustBe "UserType: Individual, UserReferenceId: "
    }

    "throw UnsupportedAffinityGroup when provided with incorrect enrolments" in {
      val mockAuthConnector: AuthConnector = mock[AuthConnector]

      when(
        mockAuthConnector.authorise(
          any[Predicate],
          any[Retrieval[Option[AffinityGroup] ~ Enrolments]]
        )(any[HeaderCarrier], any[ExecutionContext])
      ).thenReturn(
        Future.successful(
          new~(
            Some(AffinityGroup.Agent),
            Enrolments(
              Set.empty
            )
          )
        )
      )

      val authorisedAction =
        new DefaultClaimsAuthorisedAction(mockAuthConnector, bodyParser)

      val controller = new Harness(authorisedAction)
      val result = controller.onPageLoad(FakeRequest("GET", "/test")).failed
      await(result) mustBe UnsupportedAffinityGroup("Agent enrolment missing or not activated")
    }
  }
}
