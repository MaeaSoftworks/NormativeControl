package com.maeasoftworks.normativecontrol.parser.chapters.model

import org.docx4j.wml.Drawing

class Picture(val p: Int, val r: Int, val c: Int, val drawing: Drawing) {
    var title: String? = null
}