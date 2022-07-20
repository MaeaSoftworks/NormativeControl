package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.TextUtils
import org.docx4j.wml.P

class Texts(private val root: DocumentParser) {
    private var pTexts: MutableMap<String, String> = HashMap()
    private var abandonedAbbreviationsRegex =
        Regex("ур-ие|ур-ия|ур-ию|ур-ем|ур-ием|ур-ии|к-рое|к-рые|к-рая|к-рый|вм\\.|напр\\.|м\\.\\s?б\\.")

    fun getText(p: P): String {
        return pTexts[p.paraId] ?: TextUtils.getText(p).also { pTexts[p.paraId] = it }.apply {
            if (abandonedAbbreviationsRegex.findAll(this).any()) {
                root.addMistake(
                    MistakeType.TEXT_ABANDONED_ABBREVIATION_FOUND,
                    root.doc.content.indexOfFirst { it is P && it.paraId == p.paraId })
            }
        }
    }
}
