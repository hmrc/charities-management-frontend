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

package services

import models.ClaimInfo
import util.BaseSpec
import viewmodels.govuk.PaginationFluency.PaginationViewModel

class PaginationServiceSpec extends BaseSpec {

  private def makeClaimInfos(n: Int): Seq[ClaimInfo] =
    (1 to n).map(i => ClaimInfo(claimId = s"claim-$i"))

  private val baseUrl = "/dashboard"

  "PaginationService.paginateClaims" - {

    "with an empty list" - {
      "return zero records and an empty pagination view model" in {
        val result = PaginationService.paginateClaims(Seq.empty, 1, baseUrl)

        result.totalRecords shouldBe 0
        result.currentPage shouldBe 1
        result.totalPages shouldBe 0
        result.paginatedData shouldBe Seq.empty
        result.paginationViewModel.items shouldBe Nil
        result.paginationViewModel.previous shouldBe None
        result.paginationViewModel.next shouldBe None
      }
    }

    "with exactly 10 claims (one full page)" - {
      "return all 10 on page 1 with no pagination links" in {
        val claims = makeClaimInfos(10)
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)

        result.totalRecords shouldBe 10
        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.paginatedData shouldBe claims
        result.paginationViewModel.items shouldBe Nil
        result.paginationViewModel.previous shouldBe None
        result.paginationViewModel.next shouldBe None
      }
    }

    "with 11 claims (two pages)" - {
      "return first 10 on page 1 with a next link but no previous" in {
        val claims = makeClaimInfos(11)
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)

        result.totalRecords shouldBe 11
        result.currentPage shouldBe 1
        result.totalPages shouldBe 2
        result.paginatedData shouldBe claims.take(10)
        result.paginationViewModel.previous shouldBe None
        result.paginationViewModel.next.isDefined shouldBe true
        result.paginationViewModel.next.map(_.href) shouldBe Some(s"$baseUrl?page=2")
      }

      "return the remaining 1 on page 2 with a previous link but no next" in {
        val claims = makeClaimInfos(11)
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)

        result.currentPage shouldBe 2
        result.paginatedData shouldBe claims.drop(10)
        result.paginationViewModel.previous.isDefined shouldBe true
        result.paginationViewModel.previous.map(_.href) shouldBe Some(s"$baseUrl?page=1")
        result.paginationViewModel.next shouldBe None
      }
    }

    "with 30 claims (three pages)" - {
      "return correct slice for page 2 with both previous and next links" in {
        val claims = makeClaimInfos(30)
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)

        result.currentPage shouldBe 2
        result.totalPages shouldBe 3
        result.paginatedData shouldBe claims.slice(10, 20)
        result.paginationViewModel.previous.map(_.href) shouldBe Some(s"$baseUrl?page=1")
        result.paginationViewModel.next.map(_.href) shouldBe Some(s"$baseUrl?page=3")
      }

      "have a page item marked as current for the active page" in {
        val claims = makeClaimInfos(30)
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)
        val items  = result.paginationViewModel.items

        items.count(_.current) shouldBe 1
        items.find(_.current).map(_.number) shouldBe Some("2")
      }
    }

    "page range and ellipsis logic" - {
      "show only page 1 and ellipsis and last page when current page is near the start of many pages" in {
        val claims = makeClaimInfos(100) // 10 pages
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)
        val items  = result.paginationViewModel.items

        result.totalPages shouldBe 10
        items.exists(_.ellipsis) shouldBe true
        items.last.number shouldBe "10"
      }

      "show ellipsis on both sides when current page is in the middle of many pages" in {
        val claims = makeClaimInfos(100) // 10 pages
        val result = PaginationService.paginateClaims(claims, 5, baseUrl)
        val items  = result.paginationViewModel.items

        items.filter(_.ellipsis).size shouldBe 2
        items.head.number shouldBe "1"
        items.last.number shouldBe "10"
      }

      "show ellipsis before last pages when current page is near the end" in {
        val claims = makeClaimInfos(100) // 10 pages
        val result = PaginationService.paginateClaims(claims, 10, baseUrl)
        val items  = result.paginationViewModel.items

        items.exists(_.ellipsis) shouldBe true
        items.head.number shouldBe "1"
      }

      "not add ellipsis when range is adjacent to first page" in {
        val claims = makeClaimInfos(40) // 4 pages
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)
        val items  = result.paginationViewModel.items

        // pages 1,2,3 visible, no gap so no ellipsis before page 1
        items.filter(_.ellipsis).size shouldBe 0
      }
    }

    "page validation" - {
      "clamp page below 1 to page 1" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 0, baseUrl)
        result.currentPage shouldBe 1
      }

      "clamp page above totalPages to totalPages" in {
        val claims = makeClaimInfos(20) // 2 pages
        val result = PaginationService.paginateClaims(claims, 99, baseUrl)
        result.currentPage shouldBe 2
      }

      "default to page 1 when not specified" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, baseUrl = baseUrl)
        result.currentPage shouldBe 1
      }
    }

    "correct href generation" - {
      "use the baseUrl for page links" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 1, "/my-url")
        val items  = result.paginationViewModel.items

        items.filterNot(_.ellipsis).foreach { item =>
          item.href should startWith("/my-url?page=")
        }
      }

      "previous link href points to page - 1" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)
        result.paginationViewModel.previous.map(_.href) shouldBe Some(s"$baseUrl?page=1")
      }

      "next link href points to page + 1" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)
        result.paginationViewModel.next.map(_.href) shouldBe Some(s"$baseUrl?page=2")
      }
    }

    "previous/next link text" - {
      "previous link has the expected text key" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 2, baseUrl)
        result.paginationViewModel.previous.flatMap(_.text) shouldBe Some("site.pagination.previous")
      }

      "next link has the expected text key" in {
        val claims = makeClaimInfos(20)
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)
        result.paginationViewModel.next.flatMap(_.text) shouldBe Some("site.pagination.next")
      }
    }

    "exactly 1 claim" - {
      "return that claim on page 1 with empty pagination" in {
        val claims = makeClaimInfos(1)
        val result = PaginationService.paginateClaims(claims, 1, baseUrl)

        result.totalRecords shouldBe 1
        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.paginatedData shouldBe claims
        result.paginationViewModel.items shouldBe Nil
        result.paginationViewModel.previous shouldBe None
        result.paginationViewModel.next shouldBe None
      }
    }
  }
}
