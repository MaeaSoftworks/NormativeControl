package normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.HtmlElement
import normativecontrol.core.rendering.html.div
import normativecontrol.core.rendering.html.span
import normativecontrol.core.implementations.ufru.UrFUConfiguration

class RenderingTests : ShouldSpec({
    context("appender") {
        should("append") {
            with(VerificationContext(UrFUConfiguration)) {
                val ctx = RenderingContext(null)
                ctx.pointer?.type shouldBe HtmlElement.Type.DIV
                ctx.pointer?.classes!!.shouldContain("page")
                val root = ctx.pointer
                ctx append div { classes += "layer1" }
                root?.children?.size shouldBe 1
                root?.children?.list?.last()?.type shouldBe HtmlElement.Type.DIV
                root?.children?.list?.last()?.classes!!.shouldContain("layer1")
                ctx.openLastElementScope()
                ctx.pointer?.classes!!.shouldContain("layer1")
                ctx append span { }
                ctx.pointer?.type shouldBe HtmlElement.Type.DIV
                ctx.pointer?.children?.size shouldBe 1
                ctx.closeLastElementScope()
                ctx.pointer?.classes!!.shouldContain("page")
            }
        }
    }

    context("html element duplication") {
        should("duplicate 1st element at 1") {
            with(VerificationContext(UrFUConfiguration)) {
                val ctx = RenderingContext(null)
                ctx.currentPage.apply {
                    div { classes += "1" }
                    div { classes += "2" }
                }
                ctx.pageBreak(1)
                ctx.currentPage.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "2" }.toString()
            }
        }

        should("duplicate 2nd element at 2") {
            with(VerificationContext(UrFUConfiguration)) {
                val ctx = RenderingContext(null)
                ctx.currentPage.apply {
                    div { classes += "1" }
                    div {
                        classes += "2"
                        div {
                            classes += "22"
                        }
                    }
                }
                ctx.pageBreak(2)
                ctx.currentPage.children[0]!!.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "22" }.toString()
            }
        }

        should("duplicate 1st element without 2nd at 1") {
            with(VerificationContext(UrFUConfiguration)) {
                val ctx = RenderingContext(null)
                ctx.currentPage.apply {
                    div { classes += "1" }
                    div {
                        classes += "2"
                        div {
                            classes += "22"
                        }
                    }
                }
                ctx.pageBreak(1)
                ctx.currentPage.children[0].toString() shouldBe HtmlElement(HtmlElement.Type.DIV).also { it.classes += "2" }.toString()
            }
        }
    }
})