package normativecontrol.core.predefined

import normativecontrol.core.chapters.Chapter
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.traits.Implementor

/**
 * Marks handler as handler of type that can be a chapter header.
 * @property handler handler that will use this trait implementor
 */
abstract class AbstractChapterHeaderTraitImplementor(protected val handler: AbstractHandler<*>) : Implementor<ChapterHeaderTrait> {
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