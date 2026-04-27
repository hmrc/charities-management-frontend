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
import models.requests.UserType

class CharitiesRepaymentDashboardController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  @Named("orgAuth") authorisedAction: BaseAuthorisedAction,
  config: AppConfig,
  claimsConnector: ClaimsConnector,
  view: CharityRepaymentDashboardView
)(using ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = authorisedAction
    .async { implicit request =>
      request.charityUser.referenceId match {
        case Some(referenceId) if request.charityUser.userType == UserType.Organisation =>
          for
            orgName           <- getOrganisationName(referenceId)
            getClaimsResponse <- claimsConnector.retrieveUnsubmittedClaims
          yield Ok(
            view(
              Some(referenceId),
              config.makeCharityRepaymentClaimUrl,
              orgName,
              config.giftAidOtherIncomeCommunityBuildingsUrl,
              config.hmrcServicesHomeUrl,
              getClaimsResponse.claimsCount == 1
            )
          )
        case _ =>
          Future.successful(Redirect(controllers.routes.AccessDeniedController.onPageLoad))
      }
    }

  private def getOrganisationName(charityReference: String)(using HeaderCarrier): Future[Option[String]] =
    claimsConnector
      .getOrganisationName(charityReference)
      .map(_.organisationName)

}
