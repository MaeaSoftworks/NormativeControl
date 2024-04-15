package normativecontrol.core

import normativecontrol.core.chapters.Chapter
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.states.State
import normativecontrol.core.configurations.VerificationSettings

abstract class Configuration<S: State>(
    name: String,
    val startChapter: Chapter,
    val verificationSettings: VerificationSettings
) : HandlerCollection(name) {
    @Suppress("UNCHECKED_CAST") // never called in correct implementation
    open val state: S = object : State() {} as S
}