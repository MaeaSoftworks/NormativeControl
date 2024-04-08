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
    @ConfigurationInternalProperty
    @Suppress("UNCHECKED_CAST")
    final override val state: S
        get() = globalStateHolder as S

    context(VerificationContext)
    @OptIn(ConfigurationInternalProperty::class)
    inline val runState: S
        get() = state

    @RequiresOptIn(
        message = "Stateful handlers have same property in their contexts. To avoid confusions use `runState` property instead.",
        level = RequiresOptIn.Level.ERROR
    )
    private annotation class ConfigurationInternalProperty
}