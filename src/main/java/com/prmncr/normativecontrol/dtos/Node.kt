package com.prmncr.normativecontrol.dtos

import org.docx4j.wml.P

class Node : Iterable<Any> {
    lateinit var type: NodeType
    var header: P? = null
    private val content: MutableList<Any> = ArrayList()

    constructor()

    constructor(type: NodeType) {
        this.type = type
    }

    fun getContent(): List<Any> {
        return content
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