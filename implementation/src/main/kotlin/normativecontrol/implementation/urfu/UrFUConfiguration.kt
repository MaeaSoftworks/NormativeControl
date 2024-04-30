package normativecontrol.implementation.urfu

import normativecontrol.core.configurations.AbstractConfiguration
import normativecontrol.core.configurations.HandlerCollection
import normativecontrol.core.chapters.ChapterConfiguration
import normativecontrol.core.settings.RenderingSettings
import normativecontrol.core.settings.VerificationSettings

@HandlerCollection(UrFUConfiguration.NAME)
class UrFUConfiguration : AbstractConfiguration<UrFUState>(
    NAME,
    Chapters.FrontPage,
    VerificationSettings(
        ChapterConfiguration.create<Chapters>()
    ),
    RenderingSettings()
) {
    override val state: UrFUState = UrFUState()

    companion object {
        const val NAME = "UrFU"
    }
}