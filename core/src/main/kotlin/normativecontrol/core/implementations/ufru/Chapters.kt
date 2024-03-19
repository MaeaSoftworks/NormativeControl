package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.chapters.Chapter

enum class Chapters(override val names: Array<String>, override val nextChapters: (() -> Array<Chapter>)? = null): Chapter {
    FrontPage(arrayOf("ТИТУЛЬНЫЙ ЛИСТ"), { arrayOf(Annotation) }),
    Annotation(arrayOf("РЕФЕРАТ"), { arrayOf(Contents) }),
    Contents(arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"), { arrayOf(Definitions, Abbreviations, Introduction) }),
    Definitions(arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"), { arrayOf(Abbreviations, Introduction) }),
    Abbreviations(arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"), { arrayOf(Introduction) }),
    Introduction(arrayOf("ВВЕДЕНИЕ"), { arrayOf(Body) }),
    Body(arrayOf("ОСНОВНОЙ РАЗДЕЛ"), { arrayOf(Conclusion, Body) }),
    Conclusion(arrayOf("ЗАКЛЮЧЕНИЕ"), { arrayOf(References) }),
    References(arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"), { arrayOf(Appendix, References) }),
    Appendix(arrayOf("ПРИЛОЖЕНИЕ"))
}