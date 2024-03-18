package normativecontrol.core.implementations.predefined

import normativecontrol.core.abstractions.Profile
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration

data object BuiltInProfile : Profile(Chapter.Undefined, ChapterConfiguration.empty)