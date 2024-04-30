package normativecontrol.core.chapters

import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

/**
 * Structured configuration of chapters.
 * @param headers map of names of chapters to chapters themselves
 * @param names mirrored [headers] map: chapter mapped to its names
 * @param order order of chapters. Every chapter mapped to chapters that can be after it.
 */
class ChapterConfiguration private constructor(
    val headers: Map<String, Chapter>,
    val names: Map<Chapter, Array<String>>,
    private val order: Map<Chapter, Array<Chapter>?>
) {
    fun getNextChapters(chapter: Chapter): Array<Chapter> {
        return order[chapter] ?: emptyArray()
    }

    companion object {
        /**
         * Creates [ChapterConfiguration] using [Chapter] enum type.
         * @param T enum type. Should be inherited from [Chapter]
         * @return [ChapterConfiguration] based on [Chapter] enum.
         */
        @OptIn(ExperimentalStdlibApi::class)
        inline fun <reified T> create(): ChapterConfiguration where T : Enum<T>, T : Chapter {
            return createByEnumEntries(enumEntries<T>())
        }

        @PublishedApi
        internal fun <T> createByEnumEntries(enumEntries: EnumEntries<T>): ChapterConfiguration where T : Enum<T>, T : Chapter {
            val names = mutableMapOf<Chapter, Array<String>>()
            val headers = mutableMapOf<String, Chapter>()
            val order = mutableMapOf<Chapter, Array<Chapter>?>()

            enumEntries.forEach { chapter ->
                chapter.names?.let { names[chapter] = it }
                chapter.names?.forEach {
                    headers += it to chapter
                }
                chapter.nextChapters.let { if (it != null) order[chapter] = it() }
            }

            return ChapterConfiguration(headers, names, order)
        }
    }
}