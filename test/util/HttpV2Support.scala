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

package util

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.*
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}
import org.scalamock.handlers.CallHandler2

trait HttpV2Support { this: MockFactory & Matchers =>

  implicit val hc: HeaderCarrier                            = HeaderCarrier()
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val mockHttp: HttpClientV2             = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  def givenGetReturns(
    expectedUrl: String,
    response: HttpResponse
  ) =
    mockHttpGetSuccess(URL(expectedUrl))(response)

  def mockHttpGetSuccess[A](url: URL)(response: A) = {
    mockHttpGet(url).once()
    mockRequestBuilderExecute(response).once()
  }

  private def mockHttpGet(url: URL) =
    (mockHttp
      .get(_: URL)(using _: HeaderCarrier))
      .expects(url, *)
      .returning(mockRequestBuilder)

  def mockRequestBuilderExecute[A](value: A): CallHandler2[HttpReads[A], ExecutionContext, Future[A]] =
    (mockRequestBuilder
      .execute(using _: HttpReads[A], _: ExecutionContext))
      .expects(*, *)
      .returning(Future.successful(value))
}
