package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.Chapter
import ru.maeasoftworks.normativecontrol.core.abstractions.chapters.ChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.abstractions.states.AbstractRuntimeState

open class Profile(
    val startChapter: Chapter,
    val chapterConfiguration: ChapterConfiguration,
    val sharedStateFactory: (() -> AbstractRuntimeState?)? = null
)