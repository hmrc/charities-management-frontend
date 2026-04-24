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

import com.google.inject.ImplementedBy
import connectors.HttpResponseOps.*
import models.*
import org.apache.pekko.actor.ActorSystem
import play.api.{Configuration, Logging}
import play.api.libs.json.{JsNull, Json, Reads, Writes}
import play.api.libs.ws.JsonBodyWritables.*
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URL
import javax.inject.Inject
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ClaimsConnectorImpl])
trait ClaimsConnector {

  type UserId = String

  def retrieveUnsubmittedClaims(using hc: HeaderCarrier): Future[GetClaimsResponse]
}

class ClaimsConnectorImpl @Inject() (
  http: HttpClientV2,
  configuration: Configuration,
  servicesConfig: ServicesConfig,
  val actorSystem: ActorSystem
)(using
  ExecutionContext
) extends ClaimsConnector
    with Retries
    with Logging {

  val baseUrl: String = servicesConfig.baseUrl("charities-claims")

  val retryIntervals: Seq[FiniteDuration] = Retries.getConfIntervals("charities-claims", configuration)

  val contextPath: String = servicesConfig
    .getConfString("charities-claims.context-path", "charities-claims")

  val claimsApiUrl: String = s"$baseUrl$contextPath/claims"

  val rdsOrganisationNameApiUrl: String = s"$baseUrl$contextPath/getOrganisationName"

  val rdsAgentNameApiUrl: String = s"$baseUrl$contextPath/getAgentName"

  final def retrieveUnsubmittedClaims(using hc: HeaderCarrier): Future[GetClaimsResponse] =
    callCharitiesClaimsBackend[Nothing, GetClaimsResponse](
      method = "GET",
      url = s"$claimsApiUrl?claimSubmitted=false",
      payload = None
    )

  private def callCharitiesClaimsBackend[I, O](
    method: String,
    url: String,
    payload: Option[I] = None,
    noneOnNotFound: Boolean = false,
    noneValue: O = null
  )(using
    writes: Writes[I],
    reads: Reads[O],
    hc: HeaderCarrier
  ): Future[O] = {
    logger.info(s"$method $url [requestId=${hc.requestId.map(_.value).getOrElse("-")}]")
    retry(retryIntervals*)(shouldRetry, retryReason) {
      val request: RequestBuilder = method match {
        case "GET" => http.get(URL(url))
      }

      payload
        .fold(request)(p => request.withBody(Json.toJson(p)))
        .execute[HttpResponse]
    }.flatMap(response =>
      if response.status == 200 then
        response
          .parseJSON[O]()
          .fold(
            error => {
              logger.error(s"Failed to parse response from $method $url: $error")
              Future.failed(Exception(error))
            },
            Future.successful
          )
      else if noneOnNotFound && response.status == 404 then Future.successful(noneValue)
      else if response.status == 400 then
        response
          .parseJSON[ClaimError]()
          .fold(
            error => {
              logger.error(s"Failed to parse 400 error response from $method $url: $error")
              Future.failed(Exception(error))
            },
            e => {
              logger.warn(s"$method $url returned 400: ${e.getMessage}")
              Future.failed(e)
            }
          )
      else {
        logger.error(s"$method $url failed with status ${response.status}")
        Future.failed(Exception(s"Request to $method $url failed because of $response ${response.body}"))
      }
    )
  }

  given Writes[Nothing] = Writes.apply(_ => JsNull)
}
