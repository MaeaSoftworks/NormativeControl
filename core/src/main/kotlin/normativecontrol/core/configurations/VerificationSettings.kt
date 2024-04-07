package normativecontrol.core.configurations

import normativecontrol.core.abstractions.chapters.ChapterConfiguration

data class VerificationSettings(
    val chapterConfiguration: ChapterConfiguration,
    val forceStyleInlining: Boolean = false
)