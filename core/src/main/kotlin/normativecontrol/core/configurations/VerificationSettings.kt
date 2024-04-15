package normativecontrol.core.configurations

import normativecontrol.core.abstractions.chapters.ChapterConfiguration

data class VerificationSettings internal constructor(
    internal val chapterConfiguration: ChapterConfiguration,
    val forceStyleInlining: Boolean = false
)