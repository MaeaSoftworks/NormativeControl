package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

class MistakeOuter(
    val mistakeId: Long,
    val p: Int? = null,
    val r: Int? = null,
    val mistakeType: MistakeType,
    val description: String? = null
) {
    override fun toString(): String {
        val result = StringBuilder()
        if (p != null || r != null) {
            result.append("[")
        }
        if (p != null) {
            result.append("p $p")
        }
        if (r != null) {
            if (p != null) {
                result.append("; ")
            }
            result.append("r $r")
        }
        if (p != null || r != null) {
            result.append("] ")
        }
        result.append(mistakeType.ru)
        if (description != null) {
            result.append(": ${description.split('/').let { "найдено: ${it[0]}, ожидалось: ${it[1]}" }}")
        }
        return result.toString()
    }
}
