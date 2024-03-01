package normativecontrol.core.implementations.predefined

import normativecontrol.core.abstractions.Profile
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.chapters.UndefinedChapter

data object BuiltInProfile : Profile(UndefinedChapter, ChapterConfiguration { })