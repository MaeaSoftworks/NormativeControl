package normativecontrol.core.chapters

import normativecontrol.core.contexts.VerificationContext

/**
 * Marks handler as handler of type that can be a chapter header.
 */
interface ChapterHeader {
    /**
     * Check if the provided element is header of any chapter.
     * @param element element that need to be checked
     * @return [Chapter] if element is header of this chapter, else `null`.
     */
    context(VerificationContext)
    fun checkChapterStart(element: Any): Chapter?

    /**
     * Checks the correctness of order of the provided chapter.
     * Implementation should call `super.`[checkChapterOrder] at the end.
     *
     * @param target The chapter to be checked.
     */
    context(VerificationContext)
    fun checkChapterOrder(target: Chapter) {
        lastDefinedChapter = target
        chapter = target
    }
}