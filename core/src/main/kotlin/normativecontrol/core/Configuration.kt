package normativecontrol.core

import normativecontrol.core.chapters.Chapter
import normativecontrol.core.configurations.VerificationSettings
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.handlers.HandlerMapper
import normativecontrol.core.states.State

abstract class Configuration<S : State>(
    name: String,
    val startChapter: Chapter,
    val verificationSettings: VerificationSettings
) : HandlerCollection(name) {
    abstract val state: S
}