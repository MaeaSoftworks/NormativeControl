package normativecontrol.core

import normativecontrol.core.chapters.Chapter
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.settings.RenderingSettings
import normativecontrol.core.settings.VerificationSettings
import normativecontrol.core.states.State

abstract class AbstractConfiguration<S : State>(
    name: String,
    val startChapter: Chapter,
    val verificationSettings: VerificationSettings,
    val renderingSettings: RenderingSettings
) : HandlerCollection(name) {
    abstract val state: S
}