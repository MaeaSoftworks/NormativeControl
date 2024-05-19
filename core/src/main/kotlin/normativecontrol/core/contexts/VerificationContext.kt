package normativecontrol.core.contexts

import normativecontrol.core.configurations.AbstractConfiguration
import normativecontrol.core.Pointer
import normativecontrol.core.Runtime
import normativecontrol.core.chapters.Chapter
import normativecontrol.core.locales.Locales
import normativecontrol.core.mistakes.MistakeEventArgs
import normativecontrol.core.mistakes.MistakeReason
import normativecontrol.core.utils.Event
import normativecontrol.core.wrappers.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import java.math.BigInteger
import java.util.*

/**
 * Part of [Runtime] created at the beginning of file verification.
 *
 * @param runtime backlink to current [Runtime]
 */
class VerificationContext(val runtime: Runtime, mlPackage: WordprocessingMLPackage, val locale: Locales) {
    val doc: MainDocumentPart = mlPackage.mainDocumentPart
    val configuration: AbstractConfiguration<*> = runtime.configuration
    val render: RenderingContext by lazy { RenderingContext(runtime) }
    val pointer = Pointer()
    var chapter: Chapter = configuration.startChapter
    val resolver: PropertyResolver = PropertyResolver(mlPackage)
    val onMistakeEvent = Event<MistakeEventArgs>()

    private val comments: CommentsPart = doc.commentsPart ?: CommentsPart().apply {
        jaxbElement = Comments()
        doc.addTargetPart(this)
    }

    var lastMistakeId: BigInteger = BigInteger.valueOf(comments.jaxbElement.comment.size.toLong())
        private set

    /**
     * Iterates over List object in document (e.g. [ContentAccessor.getContent]).
     * @param fn function that will run on every element
     */
    @OptIn(Pointer.PointerTransformations::class)
    inline fun List<Any>.iterate(fn: (element: Any, pos: Int) -> Unit) {
        val level = pointer.size
        pointer[level] = 0
        while (pointer[level] < size) {
            fn(this[pointer[level]], pointer[level])
            pointer[level]++
        }
        pointer.clearTo(level)
    }

    @OptIn(Pointer.PointerTransformations::class)
    inline fun List<Any>.iterate(count: Int, fn: (element: Any, pos: Int) -> Unit) {
        val level = pointer.size
        pointer[level] = 0
        while (pointer[level] < count) {
            fn(this[pointer[level]], pointer[level])
            pointer[level]++
        }
        pointer.clearTo(level)
    }

    /**
     * Iterates over [ContentAccessor.getContent].
     * @param fn function that will run on every element
     */
    @OptIn(Pointer.PointerTransformations::class)
    inline fun ContentAccessor.iterate(fn: (element: Any, pos: Int) -> Unit) = content.iterate(fn)

    /**
     * Adds mistake to document.
     * @param mistakeReason reason
     * @param actual actual value
     * @param expected expected value
     */
    fun mistake(mistakeReason: MistakeReason, actual: String? = null, expected: String? = null, force: Boolean = false) {
        if (!chapter.shouldBeVerified && !force) return
        if (mistakeReason.id in configuration.state.suppressed) return

        val formattedText = if (actual != null && expected != null) {
            "${mistakeReason.description}: найдено: ${actual}, требуется: ${expected}."
        } else {
            mistakeReason.description
        }
        lastMistakeId += BigInteger.ONE

        onMistakeEvent(MistakeEventArgs(mistakeReason, lastMistakeId, expected, actual))

        val comment = createComment(lastMistakeId, formattedText)
        comments.jaxbElement.comment.add(comment)

        val commentRangeStart = CommentRangeStart().apply { this.id = lastMistakeId }
        val commentRangeEnd = CommentRangeEnd().apply { this.id = lastMistakeId }
        val runCommentReference = createRunCommentReference(lastMistakeId)
        val target = getCurrentElement()

        if (target is P) {
            val parent = target.parent as ContentAccessor
            insertComment(target, parent, commentRangeStart, commentRangeEnd, runCommentReference)
        }

        if (target is R) {
            val parent = target.parent as ContentAccessor
            insertComment(parent, parent, commentRangeStart, commentRangeEnd, runCommentReference)
        }
    }

    private fun getCurrentElement(): Any? {
        var element: Any? = doc.content
        for (i in pointer.value) {
            when (element) {
                null -> return null
                is List<*> -> element = element[i]
                is ContentAccessor -> element = element.content[i]
            }
        }
        return element
    }

    @OptIn(Pointer.PointerTransformations::class)
    private fun insertComment(
        target: ContentAccessor,
        parent: ContentAccessor,
        commentRangeStart: CommentRangeStart,
        commentRangeEnd: CommentRangeEnd,
        runCommentReference: R
    ) {
        target.content?.add(runCommentReference)
        parent.content?.add(pointer.last(), commentRangeStart)
        parent.content?.add(pointer.last() + 2, commentRangeEnd)
        pointer[pointer.depth]++
    }

    private fun createComment(commentId: BigInteger, message: String): Comments.Comment {
        return Comments.Comment().apply {
            id = commentId
            author = "normative control"
            content.add(
                P().apply {
                    paraId = "nc-${UUID.randomUUID()}"
                    content.add(
                        R().apply {
                            content.add(Text().also { text -> text.value = message })
                            rPr = RPr().apply {
                                rFonts = RFonts().apply {
                                    ascii = "Consolas"
                                    cs = "Consolas"
                                    eastAsia = "Consolas"
                                    hAnsi = "Consolas"
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    private fun createRunCommentReference(commentId: BigInteger): R {
        return R().also { run ->
            run.content.add(
                R.CommentReference().also {
                    it.id = commentId
                }
            )
        }
    }
}