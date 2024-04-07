package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.configurations.VerificationSettings

object UrFUConfiguration : Configuration<UrFURunState>(
    Chapters.FrontPage,
    VerificationSettings(
        ChapterConfiguration.create<Chapters>()
    )
) {
    override val stateFactory: StateFactory = UrFURunState
}