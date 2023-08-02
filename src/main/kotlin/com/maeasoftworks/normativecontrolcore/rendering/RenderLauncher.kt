package com.maeasoftworks.normativecontrolcore.rendering

import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import com.maeasoftworks.normativecontrolcore.rendering.model.PageSettings
import com.maeasoftworks.normativecontrolcore.rendering.model.html.HTMLElement
import com.maeasoftworks.normativecontrolcore.rendering.model.html.HTMLFile
import java.io.OutputStream

class RenderLauncher(
    private val root: DocumentParser
) {
    var html: HTMLFile = HTMLFile(
        PageSettings().apply {
            val pageSize = root.doc.contents.body.sectPr.pgSz
            width = pageSize.w.intValueExact()
            height = pageSize.h.intValueExact()
            val pageMargins = root.doc.contents.body.sectPr.pgMar
            topMargin = pageMargins.top.intValueExact()
            leftMargin = pageMargins.left.intValueExact()
            bottomMargin = pageMargins.bottom.intValueExact()
            rightMargin = pageMargins.right.intValueExact()
            autoHyphen = root.autoHyphenation
        }
    )

    fun render(stream: OutputStream) {
        val content = Renderer(root).render()
        html.content.add(content)
        for (page in html.content[0].children) {
            page.children.add(0, HTMLElement("div").withClass("page-size"))
        }
        stream.write(html.toString().toByteArray())
    }
}
