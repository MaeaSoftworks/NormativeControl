package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Profile
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext

object UrFUProfile : Profile(
    Chapters.FrontPage,
    VerificationConfiguration().initialize {
        chapterConfiguration = ChapterConfiguration.create<Chapters>()
    },
    { GlobalState() }
) {
    override val VerificationContext.globalState: GlobalState
        get() = globalStateHolder as GlobalState
}