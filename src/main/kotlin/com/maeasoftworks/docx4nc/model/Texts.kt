package com.maeasoftworks.docx4nc.model

import org.docx4j.TextUtils
import org.docx4j.wml.P

class Texts {
    private var pTexts: MutableMap<String, String> = HashMap()

    fun getText(p: P): String {
        return pTexts[p.paraId] ?: TextUtils.getText(p).also { pTexts[p.paraId] = it }
    }
}
