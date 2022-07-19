package com.maeasoftworks.docxrender.rendering.projectors

object AutoHyphenProjector: Projector {
    override fun <T> project(from: T): String {
        return if (from == true) "auto" else "none"
    }
}