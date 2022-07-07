package com.maeasoftworks.docxrender.rendering.projectors

object WeightProjector: Projector {
    override fun <T> project(from: T): String? {
        val f = from as Boolean?
        return if (f == null) null else if (f) "bold" else null
    }
}