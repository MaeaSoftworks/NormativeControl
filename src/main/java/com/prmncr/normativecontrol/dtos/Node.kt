package com.prmncr.normativecontrol.dtos

import org.docx4j.wml.P

class Node : Iterable<Any> {
    var type: NodeType? = null
    var header: P? = null
    val content: MutableList<Any> = ArrayList()

    constructor()

    constructor(type: NodeType) {
        this.type = type
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

    fun remove(i: Int) {
        content.removeAt(i)
    }
}