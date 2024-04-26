package normativecontrol.implementation.urfu

import normativecontrol.core.AbstractConfiguration
import normativecontrol.core.annotations.Configuration
import normativecontrol.core.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationSettings

@Configuration(UrFUConfiguration.NAME)
class UrFUConfiguration : AbstractConfiguration<UrFUState>(
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