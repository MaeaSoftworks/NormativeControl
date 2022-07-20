package com.maeasoftworks.livermorium.rendering.projectors

object SmallCapsProjector : Projector {
    override fun <T> project(from: T): String? {
        val f = from as Boolean?
        return if (f == null) null else if (f) "small-caps" else null
    }
}