package com.maeasoftworks.livermorium.rendering.converters

object HexColorConverter : Converter {
    override fun convert(from: String?): String? {
        return if (from != "null") "#$from" else null
    }
}