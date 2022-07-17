package com.maeasoftworks.docx4nc.model

/**
 * В этом перечислении содержатся элементы, обозначающие тип ошибки, которая возникла при обработке документа.
 *
 * @author prmncr
 */
enum class FailureType {
    /**
     * При обработке документа не возникло никаких ошибок.
     *
     * @author prmncr
     */
    NONE,

    /**
     * Не удалось прочитать файл.
     *
     * @author prmncr
     */
    FILE_READING_ERROR
}
