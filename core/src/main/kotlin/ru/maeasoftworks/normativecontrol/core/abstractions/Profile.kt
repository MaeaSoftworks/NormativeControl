package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.implementations.ufru.*

sealed class Profile(
    val startChapter: Chapter,
    val chapterConfiguration: ChapterConfiguration,
    val sharedState: AbstractSharedState? = null
) {
    data object BuiltIn : Profile(UndefinedChapter, ChapterConfiguration { })

    data object UrFU : Profile(
        FrontPageChapter,
        ChapterConfiguration {
            FrontPageChapter shouldBeNamed "ТИТУЛЬНЫЙ ЛИСТ"
            FrontPageChapter shouldBeBefore AnnotationChapter

            AnnotationChapter shouldBeNamed "РЕФЕРАТ"
            AnnotationChapter shouldBeBefore ContentsChapter

            ContentsChapter shouldBeNamed "СОДЕРЖАНИЕ" or "ОГЛАВЛЕНИЕ"
            ContentsChapter shouldBeBefore IntroductionChapter

            DefinitionsChapter shouldBeNamed "ТЕРМИНЫ И ОПРЕДЕЛЕНИЯ"
            DefinitionsChapter shouldBeBefore AbbreviationsChapter or IntroductionChapter

            AbbreviationsChapter shouldBeNamed "ПЕРЕЧЕНЬ СОКРАЩЕНИЙ И ОБОЗНАЧЕНИЙ"
            AbbreviationsChapter shouldBeBefore IntroductionChapter

            IntroductionChapter shouldBeNamed "ВВЕДЕНИЕ"
            IntroductionChapter shouldBeBefore BodyChapter

            BodyChapter shouldBeNamed "ОСНОВНОЙ РАЗДЕЛ"
            BodyChapter shouldBeBefore BodyChapter or ConclusionChapter

            ConclusionChapter shouldBeNamed "ЗАКЛЮЧЕНИЕ"
            ConclusionChapter shouldBeBefore ReferencesChapter

            ReferencesChapter shouldBeNamed "БИБЛИОГРАФИЧЕСКИЙ СПИСОК" or "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ" or "СПИСОК ЛИТЕРАТУРЫ" or "СПИСОК ИСТОЧНИКОВ"
            ReferencesChapter shouldBeBefore AppendixChapter

            AppendixChapter shouldBeNamed "ПРИЛОЖЕНИЕ"
            AppendixChapter shouldBeBefore AppendixChapter

            UndefinedChapter shouldBeNamed "НЕОПОЗНАННАЯ ЧАСТЬ"
        },
        SharedState()
    )
}