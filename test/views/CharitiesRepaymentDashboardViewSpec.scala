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
import views.html.CharityRepaymentDashboardView

class CharitiesRepaymentDashboardViewSpec extends ViewSpec {

  private val view = injectView[CharityRepaymentDashboardView]

  "CharityRepaymentDashboardView" should {

    "render title and heading correctly without reference" in {
      val doc = asDocument(view(None))

      assertTitle(doc, messages("charityRepaymentDashboard.title"))
      assertH1(doc, messages("charityRepaymentDashboard.heading"))
    }

    "render the body paragraph" in {
      val doc = asDocument(view(None))

      doc.select("p.govuk-body").first().text() mustBe messages("charityRepaymentDashboard.p")
    }

    "render the caption when uReference is provided" in {
      val ref = "ABC123"
      val doc = asDocument(view(Some(ref)))

      doc.select("span.govuk-caption-l").text() mustBe messages("charityRepaymentDashboard.caption", ref)
    }

    "not render the caption when uReference is not provided" in {
      val doc = asDocument(view(None))

      doc.select("span.govuk-caption-l").isEmpty mustBe true
    }

    "render two cards" in {
      val doc = asDocument(view(None))

      doc.select(".card-group__item").size() mustBe 2
    }

    "render the first card with correct title and description" in {
      val doc = asDocument(view(None))

      val firstCard = doc.select(".card-group__item").first()

      firstCard.select(".card__link").text() mustBe messages("charityRepaymentDashboard.card.1.heading")
      firstCard.select(".card__description").text() mustBe messages("charityRepaymentDashboard.card.1.p")
    }

    "render the second card with correct title and description" in {
      val doc = asDocument(view(None))

      val secondCard = doc.select(".card-group__item").get(1)

      secondCard.select(".card__link").text() mustBe messages("charityRepaymentDashboard.card.2.heading")
      secondCard.select(".card__description").text() mustBe messages("charityRepaymentDashboard.card.2.p")
    }

    "render the link with correct href for the second card" in {
      val doc = asDocument(view(None))

      val link = doc.select("a[href='https://www.gov.uk/government/publications/charities-online-commercial-software-suppliers']")
      link.isEmpty mustBe false
    }
  }
}