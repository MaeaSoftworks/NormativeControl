package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

/**
 * В этом классе инкапсулированы данные об ошибке, которые будет получать пользователь после
 * успешной обработки его работы (кроме mistakeId).
 *
 * @author prmncr
 */
class MistakeOuter(

    /**
     * Уникальный идентификатор ошибки, создаваемый, присваиваемый и используемый классом
     * DocumentParser.kt. Также выступает как порядковый номер.
     * В отличии от остальных полей - пользователю не возвращается.
     *
     * @see com.maeasoftworks.docx4nc.parsers.DocumentParser.kt
     *
     * @author prmncr
     */
    val mistakeId: Long,

    /**
     * Номера параграфа, в котором была допущена ошибка.
     *
     * @author prmncr
     */
    val p: Int? = null,

    /**
     * Номера прогона параграфа, в котором была допущена ошибка.
     *
     * @author prmncr
     */
    val r: Int? = null,

    /**
     * Тип допущенной ошибки.
     *
     * @author prmncr
     */
    val mistakeType: MistakeType,

    /**
     * Описание допущенной ошибки.
     *
     * @author prmncr
     */
    val description: String? = null
) {
    /**
     * Создаёт строковое представление объекта в формате JSON с помощью StringBuilder и возвращает его.
     * Пример:
     * {
     *      "paragraph-id": 91,
     *      "run-id": 0,
     *      "mistake-type": "TEXT_WHITESPACE_INCORRECT_FONT_SIZE",
     *      "description": "11/14"
     * }
     *
     * @return Строковое представление объекта в виде JSON
     *
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
