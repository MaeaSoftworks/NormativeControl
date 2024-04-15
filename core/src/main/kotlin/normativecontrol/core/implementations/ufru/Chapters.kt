package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.chapters.Chapter

internal enum class Chapters(
    override val names: Array<String>? = null,
    override val nextChapters: (() -> Array<Chapter>)? = null,
    override val prefixes: Array<String>? = null,
    override val shouldBeVerified: Boolean = true
) : Chapter {
    FrontPage(
        arrayOf("ТИТУЛЬНЫЙ ЛИСТ"),
        { arrayOf(Annotation) },
        shouldBeVerified = false
    ),

    Annotation(
        arrayOf("РЕФЕРАТ"),
        { arrayOf(Contents) },
        shouldBeVerified = false
    ),

    Contents(
        arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"),
        { arrayOf(Definitions, Abbreviations, Introduction) },
        shouldBeVerified = false
    ),

    Definitions(
        arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"),
        { arrayOf(Abbreviations, Introduction) },
        shouldBeVerified = false
    ),

    Abbreviations(
        arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"),
        { arrayOf(Introduction) },
        shouldBeVerified = false
    ),

    Introduction(
        arrayOf("ВВЕДЕНИЕ"),
        { arrayOf(Body) }
    ),

    Body(
        arrayOf("ОСНОВНОЙ РАЗДЕЛ"),
        { arrayOf(Conclusion, Body) }
    ),

    Conclusion(
        arrayOf("ЗАКЛЮЧЕНИЕ"),
        { arrayOf(References) }
    ),

    References(
        arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"),
        { arrayOf(Appendix, References) },
        shouldBeVerified = false
    ),

    Appendix(
        arrayOf("ПРИЛОЖЕНИЕ"),
        prefixes = arrayOf("ПРИЛОЖЕНИЕ"),
        shouldBeVerified = false
    )
}