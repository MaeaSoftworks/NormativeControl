package com.maeasoftworks.docx4nc.model

object ChapterMarkers {
    val markers: List<List<String>>
    val annotation = listOf("РЕФЕРАТ")
    val contents = listOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ")
    val introduction = listOf("ВВЕДЕНИЕ")
    val conclusion = listOf("ЗАКЛЮЧЕНИЕ")
    val references =
        listOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ")
    val appendix = listOf("ПРИЛОЖЕНИЕ", "ПРИЛОЖЕНИЯ")

    init {
        markers = listOf(
            ArrayList(), // front page
            annotation,
            contents,
            introduction,
            ArrayList(), // body
            conclusion,
            references,
            appendix,
        )
    }
}
