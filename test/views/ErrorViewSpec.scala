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

package views

import org.scalatestplus.play.*
import util.ViewSpec
import views.html.ErrorView

class ErrorViewSpec extends ViewSpec {

  private val view       = injectView[ErrorView]
  private val accountUrl = "https://test-url"

  "ErrorView" should {

    "render title and heading correctly" in {
      val doc = asDocument(view(accountUrl))

      assertTitle(doc, messages("error.title"))
      assertH1(doc, messages("error.heading"))
    }

    "render the error body text" in {
      val doc = asDocument(view(accountUrl))

      doc.select("p.govuk-body").first().text() mustBe messages("error.p")
    }

    "render the sign-in link with correct href" in {
      val doc = asDocument(view(accountUrl))

      doc
        .select(s"a:contains(${messages("error.signInLink")})")
        .attr("href") mustBe accountUrl
    }

    "not render a back link" in {
      val doc = asDocument(view(accountUrl))

      doc.select(".govuk-back-link").isEmpty mustBe true
    }
  }
}
