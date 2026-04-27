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

package controllers

import com.google.inject.name.Named
import config.AppConfig
import controllers.actions.BaseAuthorisedAction
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CharityRepaymentDashboardView
import connectors.ClaimsConnector

import scala.concurrent.Future
import javax.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.ExecutionContext

class CharitiesRepaymentDashboardController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  @Named("orgAuth") orgAuth: BaseAuthorisedAction,
  config: AppConfig,
  claimsConnector: ClaimsConnector,
  view: CharityRepaymentDashboardView
)(using ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = orgAuth.async { implicit request =>
    for
      orgName           <- getOrganisationName(request.charityUser.referenceId)
      getClaimsResponse <- claimsConnector.retrieveUnsubmittedClaims
    yield Ok(
      view(
        request.charityUser.referenceId,
        config.makeCharityRepaymentClaimUrl
        // orgName,
        // config.giftAidOtherIncomeCommunityBuildingsUrl,
        // config.hmrcServicesHomeUrl,
        // getClaimsResponse.claimsCount > 0
      )
    )
  }

  private def getOrganisationName(currentUser: Option[String])(using HeaderCarrier): Future[Option[String]] =
    claimsConnector
      .getOrganisationName(currentUser)
      .map(_.organisationName)

}
