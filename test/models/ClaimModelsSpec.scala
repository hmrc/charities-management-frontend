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

package models

import play.api.libs.json.{JsError, JsSuccess, Json}
import util.BaseSpec

class ClaimModelsSpec extends BaseSpec {

  // --- Donation ---

  private val fullDonation = Donation(
    donationItem = Some(1),
    donationDate = "2024-01-15",
    donationAmount = BigDecimal("100.00"),
    donorTitle = Some("Mr"),
    donorFirstName = Some("John"),
    donorLastName = Some("Smith"),
    donorHouse = Some("1 High Street"),
    donorPostcode = Some("SW1A 1AA"),
    sponsoredEvent = Some(false),
    aggregatedDonations = Some("N")
  )

  private val minimalDonation = Donation(
    donationDate = "2024-02-01",
    donationAmount = BigDecimal("50.50")
  )

  "Donation JSON format" - {

    "serialize a full Donation" in {
      val json = Json.toJson(fullDonation)
      (json \ "donationDate").as[String] shouldBe "2024-01-15"
      (json \ "donationAmount").as[BigDecimal] shouldBe BigDecimal("100.00")
      (json \ "donorFirstName").asOpt[String] shouldBe Some("John")
    }

    "serialize a minimal Donation" in {
      val json = Json.toJson(minimalDonation)
      (json \ "donationDate").as[String] shouldBe "2024-02-01"
      (json \ "donorTitle").asOpt[String] shouldBe None
    }

    "deserialize a full Donation from JSON" in {
      val json = Json.parse(
        """{
          |  "donationItem": 1,
          |  "donationDate": "2024-01-15",
          |  "donationAmount": 100.00,
          |  "donorTitle": "Mr",
          |  "donorFirstName": "John",
          |  "donorLastName": "Smith",
          |  "donorHouse": "1 High Street",
          |  "donorPostcode": "SW1A 1AA",
          |  "sponsoredEvent": false,
          |  "aggregatedDonations": "N"
          |}""".stripMargin
      )
      json.validate[Donation] shouldBe JsSuccess(fullDonation)
    }

    "deserialize a minimal Donation from JSON" in {
      val json = Json.parse("""{"donationDate":"2024-02-01","donationAmount":50.50}""")
      json.validate[Donation] shouldBe JsSuccess(minimalDonation)
    }

    "fail to deserialize when donationDate is missing" in {
      val json = Json.parse("""{"donationAmount":100.00}""")
      json.validate[Donation] shouldBe a[JsError]
    }

    "fail to deserialize when donationAmount is missing" in {
      val json = Json.parse("""{"donationDate":"2024-01-01"}""")
      json.validate[Donation] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize full Donation" in {
      Json.toJson(fullDonation).as[Donation] shouldBe fullDonation
    }

    "round-trip serialize and deserialize minimal Donation" in {
      Json.toJson(minimalDonation).as[Donation] shouldBe minimalDonation
    }
  }

  // --- CommunityBuilding ---

  private val fullCommunityBuilding = CommunityBuilding(
    communityBuildingItem = 1,
    buildingName = "The Hall",
    firstLineOfAddress = "10 Main Road",
    postcode = "AB1 2CD",
    taxYear1 = 2023,
    amountYear1 = BigDecimal("1500.00"),
    taxYear2 = Some(2024),
    amountYear2 = Some(BigDecimal("2000.00"))
  )

  private val minimalCommunityBuilding = CommunityBuilding(
    communityBuildingItem = 2,
    buildingName = "Scout Hut",
    firstLineOfAddress = "5 Park Lane",
    postcode = "CD3 4EF",
    taxYear1 = 2022,
    amountYear1 = BigDecimal("500.00")
  )

  "CommunityBuilding JSON format" - {

    "serialize a full CommunityBuilding" in {
      val json = Json.toJson(fullCommunityBuilding)
      (json \ "buildingName").as[String] shouldBe "The Hall"
      (json \ "taxYear2").asOpt[Int] shouldBe Some(2024)
      (json \ "amountYear2").asOpt[BigDecimal] shouldBe Some(BigDecimal("2000.00"))
    }

    "serialize a minimal CommunityBuilding" in {
      val json = Json.toJson(minimalCommunityBuilding)
      (json \ "buildingName").as[String] shouldBe "Scout Hut"
      (json \ "taxYear2").asOpt[Int] shouldBe None
      (json \ "amountYear2").asOpt[BigDecimal] shouldBe None
    }

    "deserialize a full CommunityBuilding from JSON" in {
      val json = Json.toJson(fullCommunityBuilding)
      json.as[CommunityBuilding] shouldBe fullCommunityBuilding
    }

    "fail to deserialize when required fields are missing" in {
      val json = Json.parse("""{"buildingName":"Hall"}""")
      json.validate[CommunityBuilding] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(fullCommunityBuilding).as[CommunityBuilding] shouldBe fullCommunityBuilding
      Json.toJson(minimalCommunityBuilding).as[CommunityBuilding] shouldBe minimalCommunityBuilding
    }
  }

  // --- GiftAidScheduleData ---

  private val giftAidScheduleData = GiftAidScheduleData(
    earliestDonationDate = Some("2023-04-01"),
    prevOverclaimedGiftAid = Some(BigDecimal("200.00")),
    totalDonations = Some(BigDecimal("5000.00")),
    donations = Seq(fullDonation, minimalDonation)
  )

  private val minimalGiftAidScheduleData = GiftAidScheduleData(
    donations = Seq.empty
  )

  "GiftAidScheduleData JSON format" - {

    "serialize with all fields" in {
      val json = Json.toJson(giftAidScheduleData)
      (json \ "earliestDonationDate").asOpt[String] shouldBe Some("2023-04-01")
      (json \ "donations").as[Seq[Donation]].size shouldBe 2
    }

    "serialize with only required fields" in {
      val json = Json.toJson(minimalGiftAidScheduleData)
      (json \ "donations").as[Seq[Donation]] shouldBe Seq.empty
      (json \ "earliestDonationDate").asOpt[String] shouldBe None
    }

    "fail to deserialize when donations is missing" in {
      val json = Json.parse("""{"earliestDonationDate":"2023-01-01"}""")
      json.validate[GiftAidScheduleData] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(giftAidScheduleData).as[GiftAidScheduleData] shouldBe giftAidScheduleData
    }
  }

  // --- OtherIncome ---

  private val otherIncome = OtherIncome(
    otherIncomeItem = 1,
    payerName = "Bank PLC",
    paymentDate = "2024-03-31",
    grossPayment = BigDecimal("1000.00"),
    taxDeducted = BigDecimal("200.00")
  )

  "OtherIncome JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(otherIncome)
      (json \ "payerName").as[String] shouldBe "Bank PLC"
      (json \ "taxDeducted").as[BigDecimal] shouldBe BigDecimal("200.00")
    }

    "deserialize from JSON" in {
      val json = Json.toJson(otherIncome)
      json.as[OtherIncome] shouldBe otherIncome
    }

    "fail to deserialize when required fields missing" in {
      val json = Json.parse("""{"payerName":"Bank"}""")
      json.validate[OtherIncome] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(otherIncome).as[OtherIncome] shouldBe otherIncome
    }
  }

  // --- OtherIncomeScheduleData ---

  private val otherIncomeScheduleData = OtherIncomeScheduleData(
    adjustmentForOtherIncomePreviousOverClaimed = BigDecimal("50.00"),
    totalOfGrossPayments = BigDecimal("1000.00"),
    totalOfTaxDeducted = BigDecimal("200.00"),
    otherIncomes = Seq(otherIncome)
  )

  "OtherIncomeScheduleData JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(otherIncomeScheduleData)
      (json \ "totalOfGrossPayments").as[BigDecimal] shouldBe BigDecimal("1000.00")
      (json \ "otherIncomes").as[Seq[OtherIncome]].size shouldBe 1
    }

    "fail to deserialize when required fields missing" in {
      val json = Json.parse("""{"totalOfGrossPayments":1000}""")
      json.validate[OtherIncomeScheduleData] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(otherIncomeScheduleData).as[OtherIncomeScheduleData] shouldBe otherIncomeScheduleData
    }
  }

  // --- GiftAidSmallDonationsSchemeClaim ---

  private val gasdsClaim = GiftAidSmallDonationsSchemeClaim(
    taxYear = 2024,
    amountOfDonationsReceived = BigDecimal("300.00")
  )

  "GiftAidSmallDonationsSchemeClaim JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(gasdsClaim)
      (json \ "taxYear").as[Int] shouldBe 2024
      (json \ "amountOfDonationsReceived").as[BigDecimal] shouldBe BigDecimal("300.00")
    }

    "deserialize from JSON" in {
      val json = Json.parse("""{"taxYear":2024,"amountOfDonationsReceived":300.00}""")
      json.as[GiftAidSmallDonationsSchemeClaim] shouldBe gasdsClaim
    }

    "fail to deserialize when taxYear is missing" in {
      val json = Json.parse("""{"amountOfDonationsReceived":300.00}""")
      json.validate[GiftAidSmallDonationsSchemeClaim] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(gasdsClaim).as[GiftAidSmallDonationsSchemeClaim] shouldBe gasdsClaim
    }
  }

  // --- GiftAidSmallDonationsSchemeDonationDetails ---

  private val gasdsDetails = GiftAidSmallDonationsSchemeDonationDetails(
    adjustmentForGiftAidOverClaimed = BigDecimal("100.00"),
    claims = Seq(gasdsClaim)
  )

  "GiftAidSmallDonationsSchemeDonationDetails JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(gasdsDetails)
      (json \ "adjustmentForGiftAidOverClaimed").as[BigDecimal] shouldBe BigDecimal("100.00")
      (json \ "claims").as[Seq[GiftAidSmallDonationsSchemeClaim]].size shouldBe 1
    }

    "deserialize from JSON" in {
      Json.toJson(gasdsDetails).as[GiftAidSmallDonationsSchemeDonationDetails] shouldBe gasdsDetails
    }

    "fail to deserialize when required fields missing" in {
      val json = Json.parse("""{"claims":[]}""")
      json.validate[GiftAidSmallDonationsSchemeDonationDetails] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(gasdsDetails).as[GiftAidSmallDonationsSchemeDonationDetails] shouldBe gasdsDetails
    }
  }

  // --- ConnectedCharity ---

  private val connectedCharity = ConnectedCharity(
    charityItem = 1,
    charityName = "Sister Charity",
    charityReference = "SC001"
  )

  "ConnectedCharity JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(connectedCharity)
      (json \ "charityName").as[String] shouldBe "Sister Charity"
      (json \ "charityReference").as[String] shouldBe "SC001"
    }

    "deserialize from JSON" in {
      val json = Json.parse("""{"charityItem":1,"charityName":"Sister Charity","charityReference":"SC001"}""")
      json.as[ConnectedCharity] shouldBe connectedCharity
    }

    "fail to deserialize when required fields missing" in {
      val json = Json.parse("""{"charityName":"Charity"}""")
      json.validate[ConnectedCharity] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(connectedCharity).as[ConnectedCharity] shouldBe connectedCharity
    }
  }

  // --- CommunityBuildingsScheduleData ---

  private val communityBuildingsScheduleData = CommunityBuildingsScheduleData(
    totalOfAllAmounts = Some(BigDecimal("3500.00")),
    communityBuildings = Seq(fullCommunityBuilding, minimalCommunityBuilding)
  )

  private val minimalCommunityBuildingsScheduleData = CommunityBuildingsScheduleData(
    communityBuildings = Seq.empty
  )

  "CommunityBuildingsScheduleData JSON format" - {

    "serialize with all fields" in {
      val json = Json.toJson(communityBuildingsScheduleData)
      (json \ "totalOfAllAmounts").asOpt[BigDecimal] shouldBe Some(BigDecimal("3500.00"))
      (json \ "communityBuildings").as[Seq[CommunityBuilding]].size shouldBe 2
    }

    "serialize with only required fields" in {
      val json = Json.toJson(minimalCommunityBuildingsScheduleData)
      (json \ "totalOfAllAmounts").asOpt[BigDecimal] shouldBe None
      (json \ "communityBuildings").as[Seq[CommunityBuilding]] shouldBe Seq.empty
    }

    "fail to deserialize when communityBuildings is missing" in {
      val json = Json.parse("""{"totalOfAllAmounts":100}""")
      json.validate[CommunityBuildingsScheduleData] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(communityBuildingsScheduleData).as[CommunityBuildingsScheduleData] shouldBe communityBuildingsScheduleData
      Json.toJson(minimalCommunityBuildingsScheduleData).as[CommunityBuildingsScheduleData] shouldBe minimalCommunityBuildingsScheduleData
    }
  }

  // --- ConnectedCharitiesScheduleData ---

  private val connectedCharitiesScheduleData = ConnectedCharitiesScheduleData(
    charities = Seq(connectedCharity, ConnectedCharity(2, "Second Charity", "SC002"))
  )

  "ConnectedCharitiesScheduleData JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(connectedCharitiesScheduleData)
      (json \ "charities").as[Seq[ConnectedCharity]].size shouldBe 2
    }

    "serialize empty charities list" in {
      val empty = ConnectedCharitiesScheduleData(charities = Seq.empty)
      val json  = Json.toJson(empty)
      (json \ "charities").as[Seq[ConnectedCharity]] shouldBe Seq.empty
    }

    "deserialize from JSON" in {
      Json.toJson(connectedCharitiesScheduleData).as[ConnectedCharitiesScheduleData] shouldBe connectedCharitiesScheduleData
    }

    "fail to deserialize when charities is missing" in {
      val json = Json.parse("""{}""")
      json.validate[ConnectedCharitiesScheduleData] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(connectedCharitiesScheduleData).as[ConnectedCharitiesScheduleData] shouldBe connectedCharitiesScheduleData
    }
  }

  // --- SubmissionDetails ---

  private val submissionDetails = SubmissionDetails(
    submissionTimestamp = "2024-01-15T10:30:00Z",
    submissionReference = "SUB-REF-001"
  )

  "SubmissionDetails JSON format" - {

    "serialize to JSON" in {
      val json = Json.toJson(submissionDetails)
      (json \ "submissionTimestamp").as[String] shouldBe "2024-01-15T10:30:00Z"
      (json \ "submissionReference").as[String] shouldBe "SUB-REF-001"
    }

    "deserialize from JSON" in {
      val json = Json.parse(
        """{"submissionTimestamp":"2024-01-15T10:30:00Z","submissionReference":"SUB-REF-001"}"""
      )
      json.as[SubmissionDetails] shouldBe submissionDetails
    }

    "fail to deserialize when required fields missing" in {
      val json = Json.parse("""{"submissionTimestamp":"2024-01-01"}""")
      json.validate[SubmissionDetails] shouldBe a[JsError]
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(submissionDetails).as[SubmissionDetails] shouldBe submissionDetails
    }
  }

  // --- OrganisationDetails (additional coverage for the many fields) ---

  private val fullOrganisationDetails = OrganisationDetails(
    nameOfCharityRegulator = NameOfCharityRegulator.EnglandAndWales,
    reasonNotRegisteredWithRegulator = Some(ReasonNotRegisteredWithRegulator.LowIncome),
    charityRegistrationNumber = Some("12345678"),
    areYouACorporateTrustee = true,
    doYouHaveCorporateTrusteeUKAddress = Some(true),
    doYouHaveAuthorisedOfficialTrusteeUKAddress = Some(false),
    nameOfCorporateTrustee = Some("ACME Trust Ltd"),
    corporateTrusteePostcode = Some("SW1A 2AA"),
    corporateTrusteeDaytimeTelephoneNumber = Some("07712345678"),
    authorisedOfficialTrusteePostcode = Some("EC1A 1BB"),
    authorisedOfficialTrusteeDaytimeTelephoneNumber = Some("07787654321"),
    authorisedOfficialTrusteeTitle = Some("Dr"),
    authorisedOfficialTrusteeFirstName = Some("Jane"),
    authorisedOfficialTrusteeLastName = Some("Doe")
  )

  "OrganisationDetails JSON format" - {

    "serialize a full OrganisationDetails" in {
      val json = Json.toJson(fullOrganisationDetails)
      (json \ "nameOfCorporateTrustee").asOpt[String] shouldBe Some("ACME Trust Ltd")
      (json \ "authorisedOfficialTrusteeFirstName").asOpt[String] shouldBe Some("Jane")
    }

    "round-trip serialize and deserialize" in {
      Json.toJson(fullOrganisationDetails).as[OrganisationDetails] shouldBe fullOrganisationDetails
    }
  }
}
