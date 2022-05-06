package com.prmncr.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.dtos.enums.NodeType
import org.docx4j.wml.P

class Node : Iterable<Any> {
    var type: NodeType? = null
    var header: P? = null
    val content: MutableList<Any> = ArrayList()
    var startPos: Int = -1

    constructor()

    constructor(type: NodeType, startPos: Int) {
        this.type = type
        this.startPos = startPos
    }

    constructor(startPos: Int) {
        this.startPos = startPos
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