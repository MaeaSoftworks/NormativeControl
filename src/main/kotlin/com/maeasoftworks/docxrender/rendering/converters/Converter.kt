package com.maeasoftworks.docxrender.rendering.converters

interface Converter {
    fun convert(from: String?): String?
}