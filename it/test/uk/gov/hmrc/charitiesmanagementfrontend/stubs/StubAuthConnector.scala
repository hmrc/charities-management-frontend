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

package uk.gov.hmrc.charitiesmanagementfrontend.stubs

import models.requests.UserType
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, Enrolment, Enrolments}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.*

import scala.concurrent.{ExecutionContext, Future}

class StubAuthConnector(userType: UserType) extends AuthConnector {
  override def authorise[A](
                             predicate: Predicate,
                             retrieval: Retrieval[A]
                           )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = {

    val affinityGroup = userType match {
      case UserType.Agent => Some(AffinityGroup.Agent)
      case UserType.Organisation => Some(AffinityGroup.Organisation)
      case _ => None
    }

    val enrolments = userType match {
      case UserType.Agent =>
        Enrolments(Set(
          Enrolment("HMRC-CHAR-AGENT")
            .withIdentifier("AGENTCHARID", "123")
        ))
      case UserType.Organisation =>
        Enrolments(Set(
          Enrolment("HMRC-CHAR-ORG")
            .withIdentifier("CHARID", "123")
        ))
      case _ =>
        Enrolments(Set.empty)
    }

    Future.successful(new~(affinityGroup, enrolments).asInstanceOf[A])
  }
}