package normativecontrol.core.abstractions.chapters

interface Chapter {
    val names: Array<String>
    val canBeAfterChapters: (() -> Array<Chapter>)?

    object Undefined : Chapter {
        override val names = arrayOf("НЕОПОЗНАННАЯ ЧАСТЬ")
        override val canBeAfterChapters = null
    }
}