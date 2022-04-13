package com.prmncr.docx4nc

import org.docx4j.model.styles.StyleTree
import org.docx4j.wml.P
import org.docx4j.wml.R

object StyleChecker {
    fun isFontWrong(p: P, r: R, tree: StyleTree, stylesheet: String?): Boolean {
        var font = ""
        if (r.rPr == null) {
            val a = tree.characterStylesTree.toList()
            //val b = tree.paragraphStylesTree.toList().first({ x -> x. == stylesheet })
            print("dsf")
        }
        if (r.rPr.rFonts == null) {
            if (p.pPr.rPr.rFonts == null) {
                val a = tree.characterStylesTree[stylesheet].data.style
            } else {
                font = p.pPr.rPr.rFonts.ascii
            }
        }
        return font != "Times New Roman"
    }

    fun isColorWrong(p: P, r: R, tree: StyleTree, stylesheet: String?): Boolean {
        var color = ""
        if (r.rPr.color == null) {
            if (p.pPr.rPr.color == null) {
                val a = tree.characterStylesTree[stylesheet].data.style
            } else {
                color = p.pPr.rPr.color.`val`
            }
        }
        return color != "auto" && color  != "000000"
    }

    fun defineRStyle(r: R, tree: StyleTree?): String? {
        var style: String? = null
        if (r.rPr == null) {
            return null
        }
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