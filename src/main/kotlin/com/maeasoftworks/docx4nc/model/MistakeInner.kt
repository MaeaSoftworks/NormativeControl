package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType


/**
 * Класс, который представляет собой ошибку в документе без айди
 *
 * @author prmncr
 */
class MistakeInner(
    /**
     * Переменная типа MistakeType, представляющая собой ошибку из MistakeType
     *
     * @author prmncr
     */
    val mistakeType: MistakeType,

    /**
     * Переменная обозначающая параграф
     *
     * @author prmncr
     */
    val p: Int? = null,

    /**
     * Переменная обозначающая «run»
     *
     * @author prmncr
     */
    val r: Int? = null,

    /**
     * Является переменной описания ошибки
     *
     * @author prmncr
     */
    val description: String? = null
)
