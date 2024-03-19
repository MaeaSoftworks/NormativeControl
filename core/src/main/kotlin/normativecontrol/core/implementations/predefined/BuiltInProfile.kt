package normativecontrol.core.implementations.predefined

import normativecontrol.core.abstractions.Profile
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.configurations.VerificationConfiguration

object BuiltInProfile : Profile(Chapter.Undefined, VerificationConfiguration().initialize {
    chapterConfiguration = ChapterConfiguration.empty
})