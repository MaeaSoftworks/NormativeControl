package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.handlers.HandlerCollection
import normativecontrol.core.abstractions.states.RunState
import normativecontrol.core.abstractions.states.Stateful
import normativecontrol.core.configurations.VerificationSettings
import normativecontrol.core.contexts.VerificationContext

abstract class Configuration<S: RunState>(
    val startChapter: Chapter,
    val verificationSettings: VerificationSettings
) : HandlerCollection, Stateful<S> {
    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    override val state: S
        get() = globalStateHolder as S
}