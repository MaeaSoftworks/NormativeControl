package normativecontrol.core.configurations

import normativecontrol.core.chapters.ChapterConfiguration

data class VerificationSettings(
    val chapterConfiguration: ChapterConfiguration,
    val forceStyleInlining: Boolean = false
)