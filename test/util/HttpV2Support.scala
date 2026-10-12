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

import org.scalatest.matchers.should.Matchers
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.*
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import java.net.URL
import scala.concurrent.Future
import play.api.libs.json.JsValue
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar

trait HttpV2Support extends MockitoSugar { this: Matchers =>

  implicit val hc: HeaderCarrier                            = HeaderCarrier()
  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val mockHttp: HttpClientV2             = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  def givenGetReturns(
    expectedUrl: String,
    response: HttpResponse
  ) =
    mockHttpGetSuccess(URL(expectedUrl))(response)

  def givenPostReturns(
    expectedUrl: String,
    requestBody: JsValue,
    response: HttpResponse
  ) =
    mockHttpPostSuccess(URL(expectedUrl), requestBody)(response)

  def mockHttpGetSuccess[A](url: URL)(response: A) = {
    mockHttpGet(url)                    // .once()
    mockRequestBuilderExecute(response) // .once()
  }

  def mockHttpPostSuccess[A](url: URL, requestBody: JsValue)(response: A) = {
    mockHttpPost(url)                       // .once()
    mockRequestBuilderWithBody(requestBody) // .once()
    mockRequestBuilderExecute(response)     // .once()
  }

  private def mockHttpGet(url: URL) = when(mockHttp.get(eqTo(url))(any())).thenReturn(mockRequestBuilder)

  def mockHttpPost[A](url: URL) = when(mockHttp.post(eqTo(url))(any())).thenReturn(mockRequestBuilder)

  def mockRequestBuilderExecute[A](value: A) =
    when(mockRequestBuilder.execute(any(), any())).thenReturn(Future.successful(value))

  def mockRequestBuilderWithBody[JsValue](
    body: JsValue
  ) =
    when(mockRequestBuilder.withBody(eqTo(body))(any(), any(), any())).thenReturn(mockRequestBuilder)

}
