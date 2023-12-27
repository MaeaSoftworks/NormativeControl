package ru.maeasoftworks.normativecontrol.core

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.annotations.Internal
import ru.maeasoftworks.normativecontrol.core.model.RenderingContext
import ru.maeasoftworks.normativecontrol.core.rendering.HtmlElement

@OptIn(Internal::class, ExperimentalKotest::class)
class RenderingTests : ShouldSpec({
    beforeTest {
        HotLoader.load()
    }

    context("simple rendering tests").config(enabled = false) {
        should("p be rendered as html <p>") {
            P()
        }
    }

    context("html element duplication") {
        should("duplicate 1st element at 1") {
            val ctx = RenderingContext(null)
            ctx.currentPage.apply {
                div { classes += "1" }
                div { classes += "2" }
            }
            ctx.pageBreak(1)
            ctx.currentPage.children[0].toString() shouldBe HtmlElement("div").also { it.classes += "2" }.toString()
        }

        should("duplicate 2nd element at 2") {
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
            ctx.currentPage.children[0].children[0].toString() shouldBe HtmlElement("div").also { it.classes += "22" }.toString()
        }

        should("duplicate 1st element without 2nd at 1") {
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
            ctx.currentPage.children[0].toString() shouldBe HtmlElement("div").also { it.classes += "2" }.toString()
        }
    }
})