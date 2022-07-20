package com.maeasoftworks.livermorium.rendering

import com.maeasoftworks.polonium.parsers.DocumentParser
import com.maeasoftworks.livermorium.model.PageSettings
import com.maeasoftworks.livermorium.model.html.HTMLElement
import com.maeasoftworks.livermorium.model.html.HTMLFile
import java.io.OutputStream

class RenderLauncher(
    val root: DocumentParser
) {
    var html: HTMLFile = HTMLFile(PageSettings().apply {
        val pageSize = root.doc.contents.body.sectPr.pgSz
        this.width = pageSize.w.intValueExact()
        this.height = pageSize.h.intValueExact()
        val pageMargins = root.doc.contents.body.sectPr.pgMar
        this.topMargin = pageMargins.top.intValueExact()
        this.leftMargin = pageMargins.left.intValueExact()
        this.bottomMargin = pageMargins.bottom.intValueExact()
        this.rightMargin = pageMargins.right.intValueExact()
        this.autoHyphen = root.autoHyphenation
    })

    fun render(stream: OutputStream) {
        val content = Renderer(root).render()
        html.content.add(content)
        for (page in html.content[0].children) {
            page.children.add(0, HTMLElement("div").withClass("page-size"))
        }
        stream.write(html.toString().toByteArray())
    }
}