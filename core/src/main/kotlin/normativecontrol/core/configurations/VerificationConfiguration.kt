package normativecontrol.core.configurations

import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.shared.lateinitVal

class VerificationConfiguration(
    val chapterConfiguration: ChapterConfiguration,
    val forceStyleInlining: Boolean = false
)