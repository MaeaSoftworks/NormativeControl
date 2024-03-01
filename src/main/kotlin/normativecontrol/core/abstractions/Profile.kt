package normativecontrol.core.abstractions

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.schema.Schema

class Profile {
    val name: String
    val startChapter: Chapter
    val chapterConfiguration: ChapterConfiguration

    @Deprecated("Use schema initialization")
    constructor(
        startChapter: Chapter,
        chapterConfiguration: ChapterConfiguration,
    ) {
        this.name = this::class.simpleName!!
        this.startChapter = startChapter
        this.chapterConfiguration = chapterConfiguration
    }

    constructor(schema: Schema) {
        this.name = schema.name
        val config = ChapterConfiguration(schema.chapters)
        chapterConfiguration = config
        startChapter = config.startChapter
    }
}