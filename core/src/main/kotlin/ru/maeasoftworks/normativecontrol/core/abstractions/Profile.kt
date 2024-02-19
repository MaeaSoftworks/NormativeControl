package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.Chapter
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.ChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.abstractions.states.AbstractGlobalState
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

abstract class Profile(
    val startChapter: Chapter,
    val chapterConfiguration: ChapterConfiguration,
    val sharedStateFactory: (() -> AbstractGlobalState?)? = null
) {
    open val VerificationContext.globalState: AbstractGlobalState
        get() = throw NotImplementedError()
}