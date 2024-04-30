package normativecontrol.implementation.urfu

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.rendering.html.HtmlElement
import normativecontrol.core.rendering.html.div
import normativecontrol.core.rendering.html.span

class RenderingTests : ShouldSpec({
    context("appender") {
        should("append") {
            with(RenderingContext(null)) {
                pointer?.type shouldBe HtmlElement.Type.DIV
                pointer?.classes!!.shouldContain("page")
                val root = pointer
                append {
                    div { classes += "layer1" }
                }
                root?.children?.size shouldBe 1
                root?.children?.list?.last()?.type shouldBe HtmlElement.Type.DIV
                root?.children?.list?.last()?.classes!!.shouldContain("layer1")
                openLastElementScope()
                pointer?.classes!!.shouldContain("layer1")
                append { span { } }
                pointer?.type shouldBe HtmlElement.Type.DIV
                pointer?.children?.size shouldBe 1
                closeLastElementScope()
                pointer?.classes!!.shouldContain("page")
            }
        }
    }

    context("html element duplication") {
        should("duplicate 1st element at 1") {
            with(RenderingContext(null)) {
                currentPage.apply {
                    div { classes += "1" }
                    div { classes += "2" }
                }
                pageBreak(0)
                currentPage.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "2" }.toString()
            }
        }

        should("duplicate 2nd element at 2") {
            with(RenderingContext(null)) {
                currentPage.apply {
                    div { classes += "1" }
                    div {
                        classes += "2"
                        div {
                            classes += "22"
                        }
                    }
                }
                pageBreak(2)
                currentPage.children[0]!!.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "22" }.toString()
            }
        }

        should("duplicate 1st element without 2nd at 1") {
            with(RenderingContext(null)) {
                currentPage.apply {
                    div { classes += "1" }
                    div {
                        classes += "2"
                        div {
                            classes += "22"
                        }
                    }
                }
                pageBreak(1)
                currentPage.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "2" }.toString()
            }
        }
    }
})