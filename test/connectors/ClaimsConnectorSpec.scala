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

package connectors

class ClaimsConnectorSpec {}
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

package connectors

import com.typesafe.config.ConfigFactory
import models.*
import org.scalamock.handlers.CallHandler
import play.api.Configuration
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import util.{BaseSpec, HttpV2Support, TestClaims}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ClaimsConnectorSpec extends BaseSpec with HttpV2Support {

  val config: Configuration = Configuration(
    ConfigFactory.parseString(
      """
        |  microservice {
        |    services {
        |      charities-claims {
        |        protocol = http
        |        host     = foo.bar.com
        |        port     = 1234
        |        retryIntervals = [10ms,50ms]
        |        context-path = "/foo-claims"
        |      }
        |   }
        |}
        |""".stripMargin
    )
  )

  val connector =
    new ClaimsConnectorImpl(
      http = mockHttp,
      servicesConfig = new ServicesConfig(config),
      configuration = config,
      actorSystem = actorSystem
    )

  def givenGetClaimEndpointReturns(
    response: HttpResponse
  ): CallHandler[Future[HttpResponse]] =
    givenGetReturns(
      expectedUrl = "http://foo.bar.com:1234/foo-claims/claims/123",
      response = response
    )

  def givenGetSubmissionClaimSummaryEndpointReturns(
    response: HttpResponse
  ): CallHandler[Future[HttpResponse]] =
    givenGetReturns(
      expectedUrl = "http://foo.bar.com:1234/foo-claims/submission-summary/123",
      response = response
    )

  given HeaderCarrier = HeaderCarrier()

  "ClaimsConnector" - {
    "retrieveUnsubmittedClaims" - {
      "have retries defined" in {
        connector.retryIntervals shouldBe Seq(FiniteDuration(10, "ms"), FiniteDuration(50, "ms"))
      }

      "should return a list of unsubmitted claims" in {
        givenGetClaimsEndpointReturns(HttpResponse(200, TestClaims.testGetClaimsResponseUnsubmittedJsonString)).once()

        await(connector.retrieveUnsubmittedClaims) shouldEqual TestClaims.testGetClaimsResponseUnsubmitted
      }

      "throw an exception if the service returns malformed JSON" in {
        givenGetClaimsEndpointReturns(HttpResponse(200, "{\"claimsCount\": 1, \"claimsList\": [{\"claimId\": 123}]"))
          .once()
        a[Exception] should be thrownBy {
          await(connector.retrieveUnsubmittedClaims)
        }
      }

      "throw an exception if the service returns wrong entity format" in {
        givenGetClaimsEndpointReturns(HttpResponse(200, "{\"claimsCount\": 1, \"claimsList\": [{\"claimId\": 123}]}"))
          .once()
        a[Exception] should be thrownBy {
          await(connector.retrieveUnsubmittedClaims)
        }
      }

      "throw an exception if the service returns 404 status" in {
        givenGetClaimsEndpointReturns(HttpResponse(404, "Bad Request")).once()
        a[Exception] should be thrownBy {
          await(connector.retrieveUnsubmittedClaims)
        }
      }

      "throw an exception if the service returns 500 status" in {
        givenGetClaimsEndpointReturns(HttpResponse(500, "")).once()
        a[Exception] should be thrownBy {
          await(connector.retrieveUnsubmittedClaims)
        }
      }

      "throw exception when 5xx response status in the third attempt" in {
        givenGetClaimsEndpointReturns(HttpResponse(500, "")).once()
        givenGetClaimsEndpointReturns(HttpResponse(499, "")).once()
        givenGetClaimsEndpointReturns(HttpResponse(469, "")).once()

        a[Exception] shouldBe thrownBy {
          await(connector.retrieveUnsubmittedClaims)
        }
      }

      "accept valid response in a second attempt" in {
        givenGetClaimsEndpointReturns(HttpResponse(500, "")).once()
        givenGetClaimsEndpointReturns(HttpResponse(200, TestClaims.testGetClaimsResponseUnsubmittedJsonString)).once()
        await(connector.retrieveUnsubmittedClaims) shouldEqual TestClaims.testGetClaimsResponseUnsubmitted
      }

      "accept valid response in a third attempt" in {
        givenGetClaimsEndpointReturns(HttpResponse(499, "")).once()
        givenGetClaimsEndpointReturns(HttpResponse(500, "")).once()
        givenGetClaimsEndpointReturns(HttpResponse(200, TestClaims.testGetClaimsResponseUnsubmittedJsonString)).once()
        await(connector.retrieveUnsubmittedClaims) shouldEqual TestClaims.testGetClaimsResponseUnsubmitted
      }
    }
  }

  "getClaim" - {
    "should send a get request and return a claim on success" in {
      givenGetClaimEndpointReturns(
        HttpResponse(200, Json.stringify(Json.toJson(TestClaims.testClaimUnsubmitted)))
      )
      await(connector.getClaim("123")) shouldEqual Some(TestClaims.testClaimUnsubmitted)
    }

    "should send a get request and return None on not found" in {
      givenGetClaimEndpointReturns(HttpResponse(404, "Not Found"))
      await(connector.getClaim("123")) shouldEqual None
    }
  }

}
