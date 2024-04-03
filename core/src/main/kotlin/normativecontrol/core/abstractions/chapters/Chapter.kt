package normativecontrol.core.abstractions.chapters

interface Chapter {
    val names: Array<String>?
    val prefixes: Array<String>?
    val nextChapters: (() -> Array<Chapter>)?
    val shouldBeVerified: Boolean

    object Undefined : Chapter {
        override val names = arrayOf("НЕОПОЗНАННАЯ ЧАСТЬ")
        override val prefixes: Array<String>? = null
        override val nextChapters = null
        override val shouldBeVerified = true
    }
}