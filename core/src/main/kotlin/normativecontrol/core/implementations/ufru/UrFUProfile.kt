package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Profile
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.contexts.VerificationContext

data object UrFUProfile: Profile(
    Chapters.FrontPage,
    ChapterConfiguration.create<Chapters>(),
    { GlobalState() }
) {
    override val VerificationContext.globalState: GlobalState
        get() = globalStateHolder as GlobalState
}