package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext

object UrFUConfiguration : Configuration(
    Chapters.FrontPage,
    VerificationConfiguration().initialize {
        chapterConfiguration = ChapterConfiguration.create<Chapters>()
    },
    { GlobalState() }
) {
    override val VerificationContext.globalState: GlobalState
        get() = globalStateHolder as GlobalState
}