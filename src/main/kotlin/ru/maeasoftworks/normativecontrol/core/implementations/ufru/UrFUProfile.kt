package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.ChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.UndefinedChapter
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

data object UrFUProfile: Profile(
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
    { GlobalState() }
) {
    override val VerificationContext.globalState: GlobalState
        get() = globalStateHolder as GlobalState
}