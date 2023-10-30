package ru.maeasoftworks.normativecontrol.core.rendering

import ru.maeasoftworks.normativecontrol.core.parsers.DocumentParser
import ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties.*
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.HtmlElement
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.html
import java.io.OutputStream

class RenderLauncher(
    private val root: DocumentParser
) {
    fun render(stream: OutputStream) {
        val html = html {
            stylesheet {
                val pageSize = root.doc.contents.body.sectPr.pgSz
                val w = pageSize.w.intValueExact()
                val h = pageSize.h.intValueExact()
                val pageMargins = root.doc.contents.body.sectPr.pgMar

                "*" += {
                    boxShadow = BoxShadow("inset 0px 0px 0px 1px red")
                    boxSizing = BoxSizing("border-box")
                    margin = Margin(0.0)
                    padding = Padding(0.0)
                }

                ".page" += {
                    width = Width((w).toDouble())
                    minHeight = MinHeight((h).toDouble())
                    paddingTop = PaddingTop((pageMargins.top.intValueExact()).toDouble())
                    paddingLeft = PaddingLeft((pageMargins.left.intValueExact()).toDouble())
                    paddingBottom = PaddingBottom((pageMargins.bottom.intValueExact()).toDouble())
                    paddingRight = PaddingRight((pageMargins.right.intValueExact()).toDouble())
                    hyphens = Hyphens(root.autoHyphenation)
                }

                ".page-size" += {
                    boxShadow = BoxShadow("inset 0px 0px 0px 1px blue")
                    boxSizing = BoxSizing("border-box")
                    position = Position("absolute")
                    width = Width((w - pageMargins.left.intValueExact() - pageMargins.right.intValueExact()).toDouble())
                    height = Height((h - pageMargins.top.intValueExact() - pageMargins.bottom.intValueExact()).toDouble())
                    zIndex = ZIndex(-10)
                }
            }
            body {
                Renderer(root).render()
            }
        }

        for (page in html.content[0].children) {
            page.children.add(0, HtmlElement("div").withClass("page-size"))
        }
        stream.write(html.toString().toByteArray())
    }
}
