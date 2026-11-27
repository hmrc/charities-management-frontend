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

package config

object Constants {
  object PaginationConfig {
    inline val recordsPerPage  = 10
    inline val maxVisiblePages = 3
  }

  object Css {
    inline val bodyCssClass = "govuk-body"

    inline val linkCssClass           = "govuk-link"
    inline val linkNotVisitedCssClass = "govuk-link--no-visited-state"

    inline val visuallyHiddenCssClass = "govuk-visually-hidden"

    inline val buttonCssClass = "govuk-button"

    inline val headingXLCssClass = "govuk-heading-xl"
    inline val headingLCssClass  = "govuk-heading-l"
    inline val headingMCssClass  = "govuk-heading-m"

    inline val gridRowCssClass           = "govuk-grid-row"
    inline val gridColumnOneHalfCssClass = "govuk-grid-column-one-half"

    inline val widthContainerCssClass = "govuk-width-container"

    inline val cardCssClass          = "card"
    inline val cardClickableCssClass = "card--clickable"
    inline val cardHeadingCssClass   = "card__heading"
    inline val cardLinkCssClass      = "card__link"
    inline val cardLinkBlockCssClass = "card__link--block"
    inline val cardDescription       = "card__description"

    inline val cardGroupCssClass     = "card-group"
    inline val cardGroupItemCssClass = "card-group__item"

    inline val charityNameCaptionCssClass = "charity-name-caption"

    inline val captionLCssClass = "govuk-caption-l"
  }
}
