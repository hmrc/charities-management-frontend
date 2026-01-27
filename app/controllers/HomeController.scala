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

import controllers.actions.ClaimsAuthorisedAction
import models.requests.UserType.{Agent, Organisation}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.Future

class HomeController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  authAction: ClaimsAuthorisedAction
) extends FrontendBaseController
    with I18nSupport {

  def landingPage: Action[AnyContent] = authAction.async { implicit request =>
    request.charUser.userType match {
      case Organisation =>
        Future.successful(Redirect(controllers.routes.CharitiesRepaymentDashboardController.onPageLoad))
      case Agent =>
        Future.successful(Redirect(controllers.routes.CharitiesRepaymentDashboardAgentController.onPageLoad))
      case _ =>
        Future.successful(Redirect(controllers.routes.AccessDeniedController.onPageLoad))
    }
  }
}
