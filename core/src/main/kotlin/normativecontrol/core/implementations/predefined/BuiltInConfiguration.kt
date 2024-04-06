package normativecontrol.core.implementations.predefined

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationConfiguration

object BuiltInConfiguration : Configuration(Chapter.Undefined, VerificationConfiguration().initialize {
    chapterConfiguration = ChapterConfiguration.empty
})