package com.maeasoftworks.normativecontrol.parser.chapters

import com.maeasoftworks.normativecontrol.parser.enums.ChapterType
import org.docx4j.wml.P

class Chapter(var startPos: Int) : Iterable<Any> {
    var type: ChapterType? = null
    var header: P? = null
    val content: MutableList<Any> = ArrayList()

    constructor(startPos: Int, content: MutableList<Any>) : this(startPos) {
        this.content.addAll(content)
    }

    fun add(item: Any) {
        content.add(item)
    }

    override fun iterator(): Iterator<Any> {
        return content.iterator()
    }

    operator fun get(i: Int): Any {
        return content[i]
    }
}