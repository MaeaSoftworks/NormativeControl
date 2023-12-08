package ru.maeasoftworks.normativecontrol.core.abstractions

abstract class AbstractChapterConfiguration(initialize: AbstractChapterConfiguration.() -> Unit) {
    val headers = mutableMapOf<String, Chapter.Companion>()
    val names = mutableMapOf<Chapter.Companion, MutableList<String>>()
    val order = mutableMapOf<Chapter.Companion, MutableList<Chapter.Companion>>()

    init {
        initialize()
    }

    fun names(builder: NameBuilder.() -> Unit) {
        builder(this.NameBuilder())
    }

    fun order(builder: OrderBuilder.() -> Unit) {
        builder(this.OrderBuilder())
    }

    /**
     * Retrieves the list of ChapterVerifier objects that must be prepended before the given chapter.
     *
     * @param chapter The chapter for which the prepended chapters are to be retrieved.
     * @return The list of ChapterVerifier objects that must be prepended before the given chapter.
     */
    fun getPrependChapter(chapter: Chapter.Companion): List<Chapter.Companion> {
        return order[chapter] ?: emptyList()
    }

    /**
     * The NameBuilder class is responsible for building and managing names of chapters.
     */
    inner class NameBuilder {
        /**
         * Sets the name for the chapter verifier.
         *
         * @param name The name to set for the chapter verifier.
         * @return The ShouldBeNamedPipe object for fluent method chaining.
         */
        infix fun Chapter.Companion.shouldBeNamed(name: String): ShouldBeNamedPipe {
            names[this] = mutableListOf(name)
            headers += name to this
            return ShouldBeNamedPipe(this)
        }

        /**
         * Represents a class for set multiple chapter names in a pipeline.
         *
         * @property target The target ChapterVerifier to be combined with.
         */
        inner class ShouldBeNamedPipe(private val target: Chapter.Companion) {
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
    }

    /**
     * The OrderBuilder class is used to construct the order in which chapters should appear.
     */
    inner class OrderBuilder {
        /**
         * Sets the order in which a chapter should appear after another chapter.
         *
         * @param chapter The chapter that should appear after the specified chapter.
         * @return The CanBeAfterPipe object for fluent method chaining.
         */
        infix fun Chapter.Companion.shouldBeAfter(chapter: Chapter.Companion): ShouldBeAfterPipe {
            order[this] = mutableListOf(chapter)
            return ShouldBeAfterPipe(this)
        }

        /**
         * Class representing a pipe that can be used to combine ChapterVerifier objects using the logical or operator.
         *
         * @property target The target ChapterVerifier to be combined with.
         * @constructor Creates a CanBeAfterPipe object with the specified target ChapterVerifier.
         */
        inner class ShouldBeAfterPipe(private val target: Chapter.Companion) {
            /**
             * Combines the current ChapterVerifier with another ChapterVerifier using the logical or operator.
             * The other ChapterVerifier is specified as the parameter of this method.
             *
             * @param another Another ChapterVerifier to be combined with the current ChapterVerifier.
             * @return Pipe with the same target.
             */
            infix fun or(another: Chapter.Companion): ShouldBeAfterPipe {
                order[target]!! += another
                return this
            }
        }
    }
}