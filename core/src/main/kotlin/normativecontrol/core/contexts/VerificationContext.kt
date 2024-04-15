package normativecontrol.core.contexts

import normativecontrol.core.Pointer
import normativecontrol.core.Configuration
import normativecontrol.core.chapters.Chapter
import normativecontrol.core.chapters.ChapterHeader
import normativecontrol.core.mistakes.MistakeReason
import normativecontrol.core.wrappers.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import java.math.BigInteger
import java.util.*

class VerificationContext(val configuration: Configuration<*>) {
    val resolver: PropertyResolver by lazy { PropertyResolver(mlPackage) }
    val render: RenderingContext by lazy { RenderingContext(doc) }
    var chapter: Chapter = configuration.startChapter
    val doc: MainDocumentPart by lazy { mlPackage.mainDocumentPart }

    var mistakeUid: String? = null

    context(ChapterHeader)
    var lastDefinedChapter: Chapter
        get() = _lastDefinedChapter
        set(value) {
            _lastDefinedChapter = value
        }

    private var _lastDefinedChapter: Chapter = configuration.startChapter

    private lateinit var mlPackage: WordprocessingMLPackage

    private lateinit var comments: CommentsPart

    var totalChildSize: Int = 0
        private set
    var pointer = Pointer()
    var mistakeId: Int = 0
        private set

    fun load(mlPackage: WordprocessingMLPackage) {
        this.mlPackage = mlPackage
        totalChildSize = doc.content.size
        comments = doc.commentsPart ?: CommentsPart().apply {
            jaxbElement = Comments()
            doc.addTargetPart(this)
        }
        mistakeId = comments.jaxbElement.comment.size
    }

    fun getCurrentElement(): Any? {
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

    inline fun List<*>.iterate(fn: (pos: Int) -> Unit) {
        val level = pointer.size
        pointer[level] = 0
        while (pointer[level] < this.size) {
            fn(pointer[level])
            pointer[level]++
        }
    }

    inline fun ContentAccessor.iterate(fn: (element: Any, pos: Int) -> Unit) {
        val level = pointer.size
        pointer[level] = 0
        while (pointer[level] < this.content.size) {
            fn(content[pointer[level]], pointer[level])
            pointer[level]++
        }
        pointer.clearTo(level)
    }

    fun mistake(mistakeReason: MistakeReason, actual: String? = null, expected: String? = null) {
        val formattedText = if (actual != null && expected != null) {
            "${mistakeReason.description}: найдено: ${actual}, требуется: ${expected}."
        } else {
            mistakeReason.description
        }
        val id = BigInteger.valueOf(mistakeId++.toLong())
        mistakeUid = "m$id"

        render.mistakeSerializer.addMistake(
            mistakeReason,
            mistakeUid!!,
            expected,
            actual
        )

        val comment = createComment(id, formattedText)
        comments.jaxbElement.comment.add(comment)

        val commentRangeStart = CommentRangeStart().apply { this.id = id }
        val commentRangeEnd = CommentRangeEnd().apply { this.id = id }
        val runCommentReference = createRunCommentReference(id)
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