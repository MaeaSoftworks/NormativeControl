package com.maeasoftworks.normativecontrol.parser.chapters.model

import com.maeasoftworks.normativecontrol.parser.enums.ChapterType
import org.docx4j.wml.P

open class Chapter(var startPos: Int) : Iterable<Any> {
    lateinit var type: ChapterType
    lateinit var header: P
    val content: MutableList<Any> = ArrayList()
    val isChapterDetected: Boolean
        get() = ::type.isInitialized
    val hasHeader: Boolean
        get() = ::header.isInitialized

    constructor(startPos: Int, content: MutableList<Any>) : this(startPos) {
        this.content.addAll(content)
    }

    fun add(item: Any) {
        content.add(item)
    }

    override fun iterator(): Iterator<Any> = content.iterator()

    operator fun get(i: Int): Any = content[i]
}