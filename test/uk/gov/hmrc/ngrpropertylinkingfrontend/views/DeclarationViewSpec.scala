package uk.gov.hmrc.ngrpropertylinkingfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import uk.gov.hmrc.ngrpropertylinkingfrontend.helpers.ViewBaseSpec
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavBarPageContents.createDefaultNavBar
import uk.gov.hmrc.ngrpropertylinkingfrontend.models.components.NavigationBarContent
import uk.gov.hmrc.ngrpropertylinkingfrontend.views.html.DeclarationView

class DeclarationViewSpec extends ViewBaseSpec {
  lazy val view: DeclarationView = inject[DeclarationView]
  lazy val navBarContent: NavigationBarContent = createDefaultNavBar()

  object Selectors {
    val title = "#main-content > div > div > form > h1"
    val p1 = "#main-content > div > div > form > p"
    val button = "#continue"
  }

  "DeclarationView" must {
    val declarationView = view(navBarContent)
    lazy implicit val document: Document = Jsoup.parse(declarationView.body)
    val htmlApply = view.apply(navBarContent).body
    val htmlRender = view.render(navBarContent, request, messages, mockConfig).body
    lazy val htmlF = view.f(navBarContent)

    "htmlF is not empty" in {
      htmlF.toString() must not be empty
    }

    "apply must be the same as render" in {
      htmlApply mustBe htmlRender
    }

    "render is not empty" in {
      htmlRender must not be empty
    }

    "should show correct content" in {
      elementText(Selectors.title) mustBe "Declaration"
      elementText(Selectors.p1) mustBe "By submitting this request to add a property, you declare that to the best of your knowledge, the information given is correct and complete."
      elementText(Selectors.button) mustBe "Accept and send"
    }
  }
}
