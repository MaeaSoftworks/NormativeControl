package normativecontrol.core.abstractions.chapters

interface Chapter {
    val names: Array<String>?
    val prefixes: Array<String>?
    val nextChapters: (() -> Array<Chapter>)?
    val shouldBeVerified: Boolean
}