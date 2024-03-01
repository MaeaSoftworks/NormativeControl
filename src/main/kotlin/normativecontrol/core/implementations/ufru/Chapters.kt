package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.chapters.Chapter

object FrontPageChapter : Chapter(
    code = "1",
    validNames = arrayOf("ТИТУЛЬНЫЙ ЛИСТ"),
    validNextChapterCodes = arrayOf(AnnotationChapter.code)
)
object AnnotationChapter : Chapter(
    code = "2",
    validNames = arrayOf("РЕФЕРАТ"),
    validNextChapterCodes = arrayOf(ContentsChapter.code)
)
object ContentsChapter : Chapter(
    code = "3",
    validNames = arrayOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ"),
    validNextChapterCodes = arrayOf(IntroductionChapter.code)
)
object DefinitionsChapter : Chapter(
    code = "4",
    validNames = arrayOf("ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"),
    validNextChapterCodes = arrayOf(AbbreviationsChapter.code, IntroductionChapter.code)
)
object AbbreviationsChapter : Chapter(
    code = "5",
    validNames = arrayOf("ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"),
    validNextChapterCodes = arrayOf(IntroductionChapter.code)
)
object IntroductionChapter : Chapter(
    code = "6",
    validNames = arrayOf("ВВЕДЕНИЕ"),
    validNextChapterCodes = arrayOf(BodyChapter.code)
)
object BodyChapter : Chapter(
    code = "7",
    validNames = arrayOf("ОСНОВНОЙ РАЗДЕЛ"),
    validNextChapterCodes = arrayOf(BodyChapter.code, ConclusionChapter.code)
)
object ConclusionChapter : Chapter(
    code = "8",
    validNames = arrayOf("ЗАКЛЮЧЕНИЕ"),
    validNextChapterCodes = arrayOf(ReferencesChapter.code)
)
object ReferencesChapter : Chapter(
    code = "9",
    validNames = arrayOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ"),
    validNextChapterCodes = arrayOf(AppendixChapter.code)
)
object AppendixChapter : Chapter(
    code = "10",
    validNames = arrayOf("ПРИЛОЖЕНИЕ"),
    validNextChapterCodes = arrayOf(AppendixChapter.code)
)