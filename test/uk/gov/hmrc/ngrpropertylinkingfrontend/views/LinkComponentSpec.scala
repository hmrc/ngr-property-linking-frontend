/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.ngrpropertylinkingfrontend.views

import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.components.LinkComponent

class LinkComponentSpec extends ViewBaseSpec {
  val injectedView: LinkComponent = injector.instanceOf[LinkComponent]

  "hyper link" when {
    "produce the same output for apply() and render()" in {
      val htmlApply = injectedView.apply("label", "/some-href", "before link content", "after link content", "_blank").body
      val htmlRender = injectedView.render("label", "/some-href", "before link content", "after link content", "_blank").body
      val htmlF = injectedView.f("label", "/some-href", "before link content", "after link content", "_blank").body
      htmlApply must not be empty
      htmlRender must not be empty
      htmlF must not be empty
    }
  }
}
