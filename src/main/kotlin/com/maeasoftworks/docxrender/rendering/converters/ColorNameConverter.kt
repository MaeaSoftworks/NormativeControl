package com.maeasoftworks.docxrender.rendering.converters

import java.util.*

object ColorNameConverter : Converter {
    override fun convert(from: String?): String? {
        return if (from != "null") from?.lowercase(Locale.getDefault()) else null
    }
}