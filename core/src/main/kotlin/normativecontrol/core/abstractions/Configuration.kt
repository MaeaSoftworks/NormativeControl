package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.handlers.HandlerCollection
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.configurations.VerificationSettings

abstract class Configuration<S: State> internal constructor(
    name: String,
    val startChapter: Chapter,
    val verificationSettings: VerificationSettings
) : HandlerCollection(name) {
    @Suppress("UNCHECKED_CAST") // never called in correct implementation
    internal open val state: S = object : State() {} as S
}