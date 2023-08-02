package com.maeasoftworks.normativecontrolcore.core.model

import com.maeasoftworks.normativecontrolcore.core.parsers.chapters.*

object ChapterMarkers {
    val markers = mapOf(
        "РЕФЕРАТ" to AnnotationParser,
        "СОДЕРЖАНИЕ" to ContentsParser,
        "ОГЛАВЛЕНИЕ" to ContentsParser,
        "ВВЕДЕНИЕ" to IntroductionParser,
        "ЗАКЛЮЧЕНИЕ" to ConclusionParser,
        "БИБЛИОГРАФИЧЕСКИЙ СПИСОК" to ReferencesParser,
        "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ" to ReferencesParser,
        "СПИСОК ЛИТЕРАТУРЫ" to ReferencesParser,
        "СПИСОК ИСТОЧНИКОВ" to ReferencesParser
    )

    val names = mapOf(
        AnnotationParser to listOf("РЕФЕРАТ"),
        ContentsParser to listOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"),
        IntroductionParser to listOf("ВВЕДЕНИЕ"),
        BodyParser to listOf("1 ОСНОВНОЙ РАЗДЕЛ"),
        ConclusionParser to listOf("ЗАКЛЮЧЕНИЕ"),
        ReferencesParser to listOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"),
        AppendixParser to listOf("ПРИЛОЖЕНИЕ"),
        UndefinedChapter to listOf("НЕОПОЗНАННАЯ ЧАСТЬ")
    )

    const val APPENDIX_NAME = "ПРИЛОЖЕНИЕ"

    val nextOf = mapOf(
        FrontPageParser to listOf(AnnotationParser),
        AnnotationParser to listOf(ContentsParser),
        ContentsParser to listOf(IntroductionParser),
        IntroductionParser to listOf(BodyParser),
        BodyParser to listOf(BodyParser, ConclusionParser),
        ConclusionParser to listOf(ReferencesParser),
        ReferencesParser to listOf(AppendixParser),
        AppendixParser to listOf(AppendixParser)
    )
}
