package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.chapters.Chapter

enum class Chapters(
    override val names: Array<String>,
    override val shouldBeVerified: Boolean,
    override val nextChapters: (() -> Array<Chapter>)? = null
): Chapter {
    FrontPage(
        arrayOf("ТИТУЛЬНЫЙ ЛИСТ"),
        false,
        { arrayOf(Annotation) }
    ),

    Annotation(
        arrayOf("РЕФЕРАТ"),
        false,
        { arrayOf(Contents) }
    ),

    Contents(
        arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"),
        false,
        { arrayOf(Definitions, Abbreviations, Introduction) }
    ),

    Definitions(
        arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"),
        false,
        { arrayOf(Abbreviations, Introduction) }
    ),

    Abbreviations(
        arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"),
        false,
        { arrayOf(Introduction) }
    ),

    Introduction(
        arrayOf("ВВЕДЕНИЕ"),
        true,
        { arrayOf(Body) }
    ),

    Body(
        arrayOf("ОСНОВНОЙ РАЗДЕЛ"),
        true,
        { arrayOf(Conclusion, Body) }
    ),

    Conclusion(
        arrayOf("ЗАКЛЮЧЕНИЕ"),
        true,
        { arrayOf(References) }
    ),

    References(
        arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"),
        false,
        { arrayOf(Appendix, References) }
    ),

    Appendix(
        arrayOf("ПРИЛОЖЕНИЕ"),
        false
    )
}