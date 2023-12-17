package ru.maeasoftworks.normativecontrol.core.abstractions

interface ChapterHeader {
    val headerRegex: Regex

    fun isChapterBodyHeader(text: String): Boolean {
        return text.matches(headerRegex)
    }

    suspend fun isHeader(element: Any): Boolean

    suspend fun detectChapterByHeader(element: Any): Chapter

    /**
     * This method checks the order of the provided chapter and updates the context accordingly.
     * Implementation should:
     * - check correctness of chapter order, using context's `lastDefinedChapter` & `chapter`;
     * - add mistakes, if they were found;
     * - update context's `lastDefinedChapter` & `chapter` according to new chapter.
     *
     * @param chapter The chapter to be checked and that will update context.
     */
    suspend fun checkChapterOrderAndUpdateContext(chapter: Chapter)
}