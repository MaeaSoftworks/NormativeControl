package com.maeasoftworks.docxrender.rendering.projectors

import org.docx4j.wml.JcEnumeration

object JustifyProjector : Projector {
    override fun <T> project(from: T): String? {
        return when (from) {
            JcEnumeration.LEFT -> "left"
            JcEnumeration.RIGHT -> "right"
            JcEnumeration.CENTER -> "center"
            JcEnumeration.BOTH -> "justify"
            else -> null
        }
    }
}