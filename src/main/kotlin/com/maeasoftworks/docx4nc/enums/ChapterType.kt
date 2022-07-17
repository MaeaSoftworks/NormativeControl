package com.maeasoftworks.docx4nc.enums

/**
 * Перечисление, определяющее типы чаптеров
 *
 * @author prmncr
 */
enum class ChapterType {

    /**
     * Чаптер определён как титульный лист
     *
     * @author prmncr
     */
    FRONT_PAGE,

    /**
     * Чаптер определён как аннотация
     *
     * @author prmncr
     */
    ANNOTATION,

    /**
     * Чаптер определён как контент
     *
     * @author prmncr
     */
    CONTENTS,

    /**
     * Чаптер определён как введение
     *
     * @author prmncr
     */
    INTRODUCTION,

    /**
     * Чаптер определён как тело
     *
     * @author prmncr
     */
    BODY,

    /**
     * Чаптер определён как заключение
     *
     * @author prmncr
     */
    CONCLUSION,

    /**
     * Чаптер определён как ссылка
     *
     * @author prmncr
     */
    REFERENCES,

    /**
     * Чаптер определён как конец
     *
     * @author prmncr
     */
    APPENDIX,

    /**
     * Чаптер не определён
     *
     * @author prmncr
     */
    UNDEFINED
}
