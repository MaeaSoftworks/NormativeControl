package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.states.AbstractGlobalState
import normativecontrol.core.contexts.VerificationContext

abstract class Profile(
    val startChapter: Chapter,
    val chapterConfiguration: ChapterConfiguration,
    val sharedStateFactory: (() -> AbstractGlobalState?)? = null
) {
    open val VerificationContext.globalState: AbstractGlobalState
        get() = throw NotImplementedError()
}