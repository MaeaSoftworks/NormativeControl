package ru.maeasoftworks.normativecontrol.core.implementations.predefined

import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.ChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.UndefinedChapter

data object BuiltInProfile : Profile(UndefinedChapter, ChapterConfiguration { })