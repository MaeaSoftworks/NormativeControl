package com.prmncr.docx4nc

import org.docx4j.model.styles.StyleTree
import org.docx4j.wml.P
import org.docx4j.wml.R

object StyleChecker {
    @JvmStatic
    fun checkFont(p: P, r: R, tree: StyleTree, stylesheet: String?): Boolean {
        val font: String
        val rStyle = defineRStyle(r, tree)
        if (r.rPr.rFonts == null) {
            if (p.pPr.rPr.rFonts == null) {
                val a = tree.characterStylesTree[rStyle].data.style
            } else {
                font = p.pPr.rPr.rFonts.ascii
            }
        }
        return r.rPr.rFonts.ascii != "Times New Roman"
    }

    fun defineRStyle(r: R, tree: StyleTree?): String? {
        var style: String? = null
        if (r.rPr.rStyle != null) {
            style = r.rPr.rStyle.getVal()
        } else {
            val pStyle = (r.parent as P).pPr.pStyle
            if (pStyle != null) {
                style = pStyle.getVal()
            }
        }
        return style
    }
}