package com.maeasoftworks.core.model

import com.maeasoftworks.core.enums.ChapterType
import org.docx4j.wml.P

/**
 * Document chapter representation
 * @param startPos chapter's start position
 */
open class Chapter(var startPos: Int) : Iterable<Any> {

    /**
     * Chapter's type.
     * @see com.maeasoftworks.core.enums.ChapterType
     */
    lateinit var type: ChapterType

    /**
     * Chapter's header
     */
    lateinit var header: P

    /**
     * Chapter's content
     */
    val content: MutableList<Any> = ArrayList()

    /**
     * Indicates whether the chapter has defined type
     */
    val isChapterDetected: Boolean
        get() = ::type.isInitialized

    /**
     * Indicates whether the chapter has a header
     */
    val hasNotHeader: Boolean
        get() = !::header.isInitialized

    /**
     * Create chapter with predefined content
     * @param startPos chapter's start position
     * @param content chapter content
     */
    constructor(startPos: Int, content: MutableList<Any>) : this(startPos) {
        this.content.addAll(content)
    }

    /**
     * `content` iterator
     * @return `content`'s iterator
     */
    override fun iterator(): Iterator<Any> = content.iterator()

    /**
     * Get paragraph by position
     * @param i paragraph position
     */
    operator fun get(i: Int): Any = content[i]

    /**
     * Add element to `content` list
     * @param item item which will be added
     */
    fun add(item: Any) {
        content.add(item)
    }
}
