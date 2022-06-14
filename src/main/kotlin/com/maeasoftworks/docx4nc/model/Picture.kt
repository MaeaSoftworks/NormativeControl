package com.maeasoftworks.docx4nc.model

import org.docx4j.wml.Drawing

data class Picture(val p: Int, val r: Int, val c: Int, val drawing: Drawing) {
    var title: String? = null
}
