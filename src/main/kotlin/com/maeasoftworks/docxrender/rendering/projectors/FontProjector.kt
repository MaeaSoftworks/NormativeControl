package com.maeasoftworks.docxrender.rendering.projectors

import org.docx4j.wml.RFonts

object FontProjector : Projector {
    override fun <T> project(from: T): String? {
        val fontSet = from as RFonts?
        val fonts = listOfNotNull(fontSet?.ascii, fontSet?.cs, fontSet?.eastAsia, fontSet?.hAnsi)
        return if (fonts.isNotEmpty()) if (fonts.drop(1).all { it == fonts[0] }) fonts[0] else null
        else if (fontSet == null) null else fonts.joinToString(",")
    }
}