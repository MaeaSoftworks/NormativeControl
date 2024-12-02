package normativecontrol.core.settings

import normativecontrol.core.chapters.ChapterConfiguration

data class VerificationSettings(
    /**
     * @sample normativecontrol.implementation.urfu.UrFUConfiguration
     */
    val chapterConfiguration: ChapterConfiguration
)