package com.maeasoftworks.docxrender

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import com.maeasoftworks.docxrender.html.HTMLFile
import java.io.OutputStream

class Renderer(
    val root: DocumentParser
) {
    private val renderers: MutableList<ChapterRenderer> = mutableListOf()
    var html: HTMLFile = HTMLFile(PageSettings().apply {
        val pageSize = root.doc.contents.body.sectPr.pgSz
        this.width = pageSize.w.intValueExact()
        this.height = pageSize.h.intValueExact()
        val pageMargins = root.doc.contents.body.sectPr.pgMar
        this.topMargin = pageMargins.top.intValueExact()
        this.leftMargin = pageMargins.left.intValueExact()
        this.bottomMargin = pageMargins.bottom.intValueExact()
        this.rightMargin = pageMargins.right.intValueExact()
    })

    init {
        for (parser in root.parsers) {
            renderers.add(ChapterRenderer(parser))
        }
    }

    fun render(stream: OutputStream, chapter: Int = -1) {
        if (chapter != -1) {
            html.content += renderers[chapter].render()
        } else {
            for (renderer in renderers) {
                html.content += renderer.render()
            }
        }
        stream.write(html.toString().toByteArray())
    }
}