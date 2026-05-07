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

import com.google.inject.Inject
import com.google.inject.name.Named
import config.AppConfig
import connectors.ClaimsConnector
import controllers.actions.BaseAuthorisedAction
import forms.YesNoFormProvider
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeleteRepaymentClaimView

import scala.concurrent.{ExecutionContext, Future}

class DeleteRepaymentClaimController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  @Named("identifyAuth") authorisedAction: BaseAuthorisedAction,
  config: AppConfig,
  claimsConnector: ClaimsConnector,
  view: DeleteRepaymentClaimView,
  formProvider: YesNoFormProvider
)(using ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  val form: Form[Boolean] = formProvider("deleteRepaymentClaim.error.required")

  def onPageLoad(claimId: String): Action[AnyContent] = authorisedAction
    .async { implicit request =>
      request.charityUser.referenceId match {
        case Some(referenceId) =>
          for agentName <- claimsConnector.getAgentName(referenceId).map(_.agentName)
          yield Ok(view(form, referenceId, agentName))
        case _ =>
          Future.successful(Redirect(controllers.routes.AccessDeniedController.onPageLoad))
      }
    }

  def onSubmit(claimId: String): Action[AnyContent] = authorisedAction
    .async { implicit request =>
      request.charityUser.referenceId match {
        case Some(referenceId) =>
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                for agentName <- claimsConnector.getAgentName(referenceId).map(_.agentName)
                yield BadRequest(view(formWithErrors, claimId, agentName)),
              {
                case true =>
                  claimsConnector.deleteClaim(claimId).flatMap { response =>
                    if true then {
                      Future.successful(Redirect(controllers.routes.CharitiesRepaymentDashboardController.onPageLoad))
                    } else {
                      logger
                        .error(
                          s"Failed to delete claim with claimId: $claimId - backend returned success: false"
                        )
                      Future.failed(
                        new RuntimeException(
                          s"Failed to delete claim with claimId: $claimId - backend returned success: false"
                        )
                      )
                    }
                  }
                case false =>
                  Future.successful(Redirect(controllers.routes.CharitiesRepaymentDashboardController.onPageLoad))
              }
            )
        case _ =>
          Future.successful(Redirect(controllers.routes.AccessDeniedController.onPageLoad))
      }
    }
}
