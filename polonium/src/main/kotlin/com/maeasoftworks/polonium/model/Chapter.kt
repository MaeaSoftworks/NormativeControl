package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.ChapterType
import org.docx4j.wml.P

/**
 * Document chapter representation
 * @param startPos chapter's start position
 * @author prmncr
 */
open class Chapter(var startPos: Int) : Iterable<Any> {

    /**
     * Chapter's type.
     * @see com.maeasoftworks.polonium.enums.ChapterType
     * @author prmncr
     */
    lateinit var type: ChapterType

    /**
     * Chapter's header
     * @author prmncr
     */
    lateinit var header: P

    /**
     * Chapter's content
     * @author prmncr
     */
    val content: MutableList<Any> = ArrayList()

    /**
     * Indicates whether the chapter has defined type
     * @author prmncr
     */
    val isChapterDetected: Boolean
        get() = ::type.isInitialized

    /**
     * Indicates whether the chapter has a header
     * @author prmncr
     */
    val hasNotHeader: Boolean
        get() = !::header.isInitialized

    /**
     * Create chapter with predefined content
     * @param startPos chapter's start position
     * @param content chapter content
     * @author prmncr
     */
    constructor(startPos: Int, content: MutableList<Any>) : this(startPos) {
        this.content.addAll(content)
    }

    /**
     * <code>content</code> iterator
     * @return <code>content</code> iterator
     * @author prmncr
     */
    override fun iterator(): Iterator<Any> = content.iterator()

    /**
     * Get paragraph by position
     * @param i paragraph position
     * @author prmncr
     */
    operator fun get(i: Int): Any = content[i]

    /**
     * Add element to <code>content</code> list
     * @param item item which will be added
     * @author prmncr
     */
    fun add(item: Any) {
        content.add(item)
    }
}
