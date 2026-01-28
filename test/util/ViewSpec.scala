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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatestplus.play.*
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.*
import play.api.mvc.*
import play.api.test.*
import play.api.test.Helpers.*

import scala.reflect.ClassTag

trait ViewSpec extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val request: Request[AnyContent] = FakeRequest()
  implicit lazy val messages: Messages           = app.injector.instanceOf[MessagesApi].preferred(request)

  protected def asDocument(html: play.twirl.api.Html): Document = Jsoup.parse(contentAsString(html))

  protected def assertTitle(doc: Document, expectedTitle: String): Unit = doc.title() mustBe expectedTitle

  protected def assertH1(doc: Document, expectedHeading: String): Unit = doc.select("h1").text() mustBe expectedHeading

  protected def injectView[T: ClassTag]: T = app.injector.instanceOf[T]
}
