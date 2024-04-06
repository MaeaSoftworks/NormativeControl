package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.states.AbstractGlobalState
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext

abstract class Configuration(
    val startChapter: Chapter,
    val verificationConfiguration: VerificationConfiguration,
    val sharedStateFactory: (() -> AbstractGlobalState?)? = null
) {
    open val VerificationContext.globalState: AbstractGlobalState
        get() = throw NotImplementedError()

    abstract fun mapHandler(element: Any?): Handler<*, *, *>?
}