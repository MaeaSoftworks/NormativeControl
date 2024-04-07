package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.states.RunState
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext

abstract class Configuration<S: RunState>(
    val startChapter: Chapter,
    val verificationConfiguration: VerificationConfiguration
) : HandlerCollection {
    abstract fun createRunState(): S

    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    val runState: S
        get() = globalStateHolder as S
}