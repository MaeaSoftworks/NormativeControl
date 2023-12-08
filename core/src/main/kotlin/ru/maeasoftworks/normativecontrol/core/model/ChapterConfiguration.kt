package ru.maeasoftworks.normativecontrol.core.model

import ru.maeasoftworks.normativecontrol.core.abstractions.AbstractChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.*

object ChapterConfiguration: AbstractChapterConfiguration({
    names {
        AnnotationVerifier shouldBeNamed "РЕФЕРАТ"
        ContentsVerifier shouldBeNamed "СОДЕРЖАНИЕ" or "ОГЛАВЛЕНИЕ"
        IntroductionVerifier shouldBeNamed "ВВЕДЕНИЕ"
        BodyVerifier shouldBeNamed "ОСНОВНОЙ РАЗДЕЛ"
        ConclusionVerifier shouldBeNamed "ЗАКЛЮЧЕНИЕ"
        ReferencesVerifier shouldBeNamed "БИБЛИОГРАФИЧЕСКИЙ СПИСОК" or "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ" or "СПИСОК ЛИТЕРАТУРЫ" or "СПИСОК ИСТОЧНИКОВ"
        AppendixVerifier shouldBeNamed "ПРИЛОЖЕНИЕ"
        UndefinedVerified shouldBeNamed "НЕОПОЗНАННАЯ ЧАСТЬ"
    }

    order {
        FrontPageVerifier shouldBeAfter AnnotationVerifier
        AnnotationVerifier shouldBeAfter ContentsVerifier
        ContentsVerifier shouldBeAfter IntroductionVerifier
        IntroductionVerifier shouldBeAfter BodyVerifier
        BodyVerifier shouldBeAfter BodyVerifier or ConclusionVerifier
        ConclusionVerifier shouldBeAfter ReferencesVerifier
        ReferencesVerifier shouldBeAfter AppendixVerifier
        AppendixVerifier shouldBeAfter AppendixVerifier
    }
}) {
    const val APPENDIX_NAME = "ПРИЛОЖЕНИЕ"
}