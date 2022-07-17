package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

/**
 * Mistake representation with `mistakeId` for external using.
 * @author prmncr
 */
data class MistakeOuter(

    /**
     * Mistake ordinal id
     * @author prmncr
     */
    val mistakeId: Long,

    /**
     * Index of mistake on p-layer
     * @see com.maeasoftworks.docx4nc.samples.Philosophy_of_Layers
     * @author prmncr
     */
    val p: Int? = null,

    /**
     * Index of mistake on r-layer
     * @see com.maeasoftworks.docx4nc.samples.Philosophy_of_Layers
     * @author prmncr
     */
    val r: Int? = null,

    /**
     * Mistake type
     * @see com.maeasoftworks.docx4nc.enums.MistakeType
     * @author prmncr
     */
    val mistakeType: MistakeType,

    /**
     * Mistake description. Unfortunately, in Russian.
     *
     * @author prmncr
     */
    val description: String? = null
) {
    /**
     * Pretty string representation of mistake.
     *
     * Output format: `[p $p r $r] $mistakeType: $description`
     *
     * Output example: `[p 23 r 3] Некорректный межстрочный интервал текста: найдено: 1, ожидалось: 1.5`
     * @return string representation of mistake
     * @author prmncr
     */
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
