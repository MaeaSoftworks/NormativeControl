package com.maeasoftworks.normativecontrol.parser

object HeadersKeywords {
    val keywordsBySector: MutableList<List<String>> = ArrayList()
    val annotation = listOf("РЕФЕРАТ")
    val contents = listOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ")
    val introduction = listOf("ВВЕДЕНИЕ")
    val conclusion = listOf("ЗАКЛЮЧЕНИЕ")
    val references =
        listOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ")
    val appendix = listOf("ПРИЛОЖЕНИЕ", "ПРИЛОЖЕНИЯ")

    init {
        keywordsBySector.add(ArrayList()) // front page
        keywordsBySector.add(annotation)
        keywordsBySector.add(contents)
        keywordsBySector.add(introduction)
        keywordsBySector.add(ArrayList()) // body
        keywordsBySector.add(conclusion)
        keywordsBySector.add(references)
        keywordsBySector.add(appendix)
    }
}