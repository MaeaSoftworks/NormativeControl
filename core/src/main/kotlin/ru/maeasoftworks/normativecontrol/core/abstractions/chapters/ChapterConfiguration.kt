package ru.maeasoftworks.normativecontrol.core.abstractions.chapters

class ChapterConfiguration(initialize: ChapterConfiguration.() -> Unit) {
    val headers = mutableMapOf<String, Chapter>()
    val names = mutableMapOf<Chapter, MutableList<String>>()
    val order = mutableMapOf<Chapter, MutableList<Chapter>>()

    init {
        initialize()
    }

    /**
     * Retrieves the list of ChapterVerifier objects that must be prepended before the given chapter.
     *
     * @param chapter The chapter for which the prepended chapters are to be retrieved.
     * @return The list of ChapterVerifier objects that must be prepended before the given chapter.
     */
    fun getPrependChapter(chapter: Chapter): List<Chapter> {
        return order[chapter] ?: emptyList()
    }

    /**
     * Sets the name for the chapter verifier.
     *
     * @param name The name to set for the chapter verifier.
     * @return The ShouldBeNamedPipe object for fluent method chaining.
     */
    infix fun Chapter.shouldBeNamed(name: String): ShouldBeNamedPipe {
        names[this] = mutableListOf(name)
        headers += name to this
        return ShouldBeNamedPipe(this)
    }

    /**
     * Represents a class for set multiple chapter names in a pipeline.
     *
     * @property target The target ChapterVerifier to be combined with.
     */
    inner class ShouldBeNamedPipe(private val target: Chapter) {
        /**
         * Adds another string to the "names" map and updates the "headers" list.
         *
         * @param another the string to be added to the "names" map.
         * @return Pipe with the same target.
         */
        infix fun or(another: String): ShouldBeNamedPipe {
            names[target]!! += another
            headers += another to target
            return this
        }
    }

    /**
     * Sets the order in which a chapter should appear after another chapter.
     *
     * @param chapter The chapter that should appear after the specified chapter.
     * @return The CanBeAfterPipe object for fluent method chaining.
     */
    infix fun Chapter.shouldBeBefore(chapter: Chapter): ShouldBeAfterPipe {
        order[this] = mutableListOf(chapter)
        return ShouldBeAfterPipe(this)
    }

    /**
     * Class representing a pipe that can be used to combine ChapterVerifier objects using the logical or operator.
     *
     * @property target The target ChapterVerifier to be combined with.
     * @constructor Creates a CanBeAfterPipe object with the specified target ChapterVerifier.
     */
    inner class ShouldBeAfterPipe(private val target: Chapter) {
        /**
         * Combines the current ChapterVerifier with another ChapterVerifier using the logical or operator.
         * The other ChapterVerifier is specified as the parameter of this method.
         *
         * @param another Another ChapterVerifier to be combined with the current ChapterVerifier.
         * @return Pipe with the same target.
         */
        infix fun or(another: Chapter): ShouldBeAfterPipe {
            order[target]!! += another
            return this
        }
    }
}