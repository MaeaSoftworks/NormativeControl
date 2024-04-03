package normativecontrol.core.abstractions.chapters

import normativecontrol.core.contexts.VerificationContext

interface ChapterHeader {
    context(VerificationContext)
    fun checkChapterStart(element: Any): Chapter?

    /**
     * This method checks the order of the provided chapter and updates the context accordingly.
     * Implementation should:
     * - check correctness of chapter order, using context's `lastDefinedChapter` & `chapter`;
     * - add mistakes, if they were found;
     * - update context's `lastDefinedChapter` & `chapter` according to new chapter.
     *
     * @param target The chapter to be checked and that will update context.
     */
    context(VerificationContext)
    fun checkChapterOrderAndUpdateContext(target: Chapter)
}