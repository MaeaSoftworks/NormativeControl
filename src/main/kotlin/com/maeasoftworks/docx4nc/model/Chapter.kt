package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.ChapterType
import org.docx4j.wml.P

/**
 * Класс Chapter – является классом частей(глав)
 *
 * @author prmncr
 */
open class Chapter(var startPos: Int) : Iterable<Any> {

    /**
     * Тип переменной ChapterType(FRONT_PAGE, ANNOTATION и тд.), которая обозначает тип главы
     *
     * @author prmncr
     */
    lateinit var type: ChapterType

    /**
     * Является параграфом взятым из библиотеки docx4j
     *
     * @author prmncr
     */
    lateinit var header: P

    /**
     * Является изменяемым списком в котором хранятся главы
     *
     * @author prmncr
     */
    val content: MutableList<Any> = ArrayList()

    /**
     * Переменная типа Boolean, которая сигнализирует о нахождении главы
     *
     * @author prmncr
     */
    val isChapterDetected: Boolean
        get() = ::type.isInitialized

    /**
     * Переменная типа Boolean, которая сигнализирует о нахождении заголовка
     *
     * @author prmncr
     */
    val hasHeader: Boolean
        get() = ::header.isInitialized

    constructor(startPos: Int, content: MutableList<Any>) : this(startPos) {
        this.content.addAll(content)
    }

    /**
     * Функция которая добавляет переменные в список content
     *
     * @author prmncr
     */
    fun add(item: Any) {
        content.add(item)
    }

    /**
     * Функция итератора которая «пробегает» по списку content
     *
     * @author prmncr
     */
    override fun iterator(): Iterator<Any> = content.iterator()

    /**
     * Функция которая позволяет получить любую переменную, имея её айди, из списка content
     *
     * @author prmncr
     */
    operator fun get(i: Int): Any = content[i]
}
