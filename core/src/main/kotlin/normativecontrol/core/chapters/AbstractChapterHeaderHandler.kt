package normativecontrol.core.chapters

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.traits.ChapterHeaderHandler
import normativecontrol.core.traits.Implementor

/**
 * Marks handler as handler of type that can be a chapter header.
 */
abstract class AbstractChapterHeaderHandler(protected val handler: AbstractHandler<*>): Implementor<ChapterHeaderHandler> {
    init {
        handler.events.beforeHandle.subscribe { element ->
            with(handler.ctx) {
                val chapter = checkChapterStart(element)
                if (chapter != null) {
                    checkChapterOrder(chapter)
                }
            }
        }
    }

    /**
     * Check if the provided element is header of any chapter.
     * @param element element that need to be checked
     * @return [Chapter] if element is header of this chapter, else `null`.
     */
    context(VerificationContext)
    abstract fun checkChapterStart(element: Any): Chapter?

    /**
     * Checks the correctness of order of the provided chapter.
     *
     * @param target The chapter to be checked.
     */
    context(VerificationContext)
    abstract fun checkChapterOrder(target: Chapter)
}