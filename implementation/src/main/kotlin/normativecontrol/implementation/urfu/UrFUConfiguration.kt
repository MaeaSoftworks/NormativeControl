package normativecontrol.implementation.urfu

import normativecontrol.core.Configuration
import normativecontrol.core.chapters.ChapterConfiguration
import normativecontrol.core.annotations.HandlerGroup
import normativecontrol.core.configurations.VerificationSettings

@HandlerGroup(UrFUConfiguration.NAME)
class UrFUConfiguration : Configuration<UrFUState>(
    NAME,
    Chapters.FrontPage,
    VerificationSettings(
        ChapterConfiguration.create<Chapters>()
    )
) {
    override val state: UrFUState = UrFUState()

    companion object {
        const val NAME = "UrFU"
    }
}