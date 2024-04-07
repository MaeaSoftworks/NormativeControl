package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.HandlerCollection
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationConfiguration

object UrFUConfiguration : Configuration<GlobalState>(
    Chapters.FrontPage,
    VerificationConfiguration(
        ChapterConfiguration.create<Chapters>()
    )
) {
    override fun createRunState(): GlobalState {
        return GlobalState()
    }
}