package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.chapters.Chapter

enum class Chapters(override val names: Array<String>, override val canBeAfterChapters: (() -> Array<Chapter>)? = null): Chapter {
    FrontPage(arrayOf("ТИТУЛЬНЫЙ ЛИСТ")),
    Annotation(arrayOf("РЕФЕРАТ"), { arrayOf(FrontPage) }),
    Contents(arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"), { arrayOf(Annotation) }),
    Definitions(arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"), { arrayOf(Contents) }),
    Abbreviations(arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"), { arrayOf(Contents, Definitions) }),
    Introduction(arrayOf("ВВЕДЕНИЕ"), { arrayOf(Contents, Definitions, Abbreviations) }),
    Body(arrayOf("ОСНОВНОЙ РАЗДЕЛ"), { arrayOf(Introduction, Body) }),
    Conclusion(arrayOf("ЗАКЛЮЧЕНИЕ"), { arrayOf(Body) }),
    References(arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"), { arrayOf(Conclusion) }),
    Appendix(arrayOf("ПРИЛОЖЕНИЕ"), { arrayOf(References, Appendix) })
}