package normativecontrol.core.abstractions.chapters

import kotlin.enums.enumEntries

data class ChapterConfiguration(
    val headers: Map<String, Chapter>,
    val names: Map<Chapter, Array<String>>,
    val order: Map<Chapter, Array<Chapter>?>
) {
    fun getNextChapters(chapter: Chapter): Array<Chapter> {
        return order[chapter] ?: emptyArray()
    }

    companion object {
        val empty: ChapterConfiguration
            get() = ChapterConfiguration(emptyMap(), emptyMap(), emptyMap())

        @OptIn(ExperimentalStdlibApi::class)
        inline fun <reified T> create(): ChapterConfiguration where T : Enum<T>, T : Chapter {
            val calculatedNames = mutableMapOf<Chapter, Array<String>>()
            val calculatedHeaders = mutableMapOf<String, Chapter>()
            val calculatedOrder = mutableMapOf<Chapter, Array<Chapter>?>()

            enumEntries<T>().forEach { chapter ->
                calculatedNames[chapter] = chapter.names
                chapter.names.forEach {
                    calculatedHeaders += it to chapter
                }
                chapter.nextChapters.let { if (it != null) calculatedOrder[chapter] = it() }
            }

            return ChapterConfiguration(calculatedHeaders, calculatedNames, calculatedOrder)
        }
    }
}