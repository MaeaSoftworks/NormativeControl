package normativecontrol.core.chapters

/**
 * Base class for chapters.
 */
interface Chapter {
    /**
     * Possible chapter headers.
     * Can be `null` if chapter will be detected by prefixes.
     */
    val names: Array<String>?

    /**
     * Possible chapter headers' prefixes.
     * Can be `null` if chapter will be detected by names.
     */
    val prefixes: Array<String>?

    /**
     * Lazy list of chapters that can be after this chapter.
     * Can be `null` if this is end chapter.
     */
    val nextChapters: (() -> Array<Chapter>)?

    /**
     * `true` if chapter need to be verified, else `false`.
     */
    val shouldBeVerified: Boolean
}