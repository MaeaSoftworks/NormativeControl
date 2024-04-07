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
        @OptIn(ExperimentalStdlibApi::class)
        inline fun <reified T> create(): ChapterConfiguration where T : Enum<T>, T : Chapter {
            val calculatedNames = mutableMapOf<Chapter, Array<String>>()
            val calculatedHeaders = mutableMapOf<String, Chapter>()
            val calculatedOrder = mutableMapOf<Chapter, Array<Chapter>?>()

            enumEntries<T>().forEach { chapter ->
                chapter.names?.let { calculatedNames[chapter] = it }
                chapter.names?.forEach {
                    calculatedHeaders += it to chapter
                }
                chapter.nextChapters.let { if (it != null) calculatedOrder[chapter] = it() }
            }

            return ChapterConfiguration(calculatedHeaders, calculatedNames, calculatedOrder)
        }
    }
}