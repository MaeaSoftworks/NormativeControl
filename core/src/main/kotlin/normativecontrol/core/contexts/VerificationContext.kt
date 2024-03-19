package normativecontrol.core.contexts

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import normativecontrol.core.abstractions.*
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.states.AbstractGlobalState
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.mistakes.MistakeReason
import normativecontrol.core.abstractions.states.PointerState
import normativecontrol.core.utils.PropertyResolver
import java.math.BigInteger
import java.util.*

class VerificationContext(val profile: Profile) {
    val resolver: PropertyResolver by lazy { PropertyResolver(mlPackage) }
    val render: RenderingContext by lazy { RenderingContext(doc) }
    var chapter: Chapter = profile.startChapter
    val doc: MainDocumentPart by lazy { mlPackage.mainDocumentPart }
    val states = mutableMapOf<State.Key, State>()
    var mistakeUid: String? = null
    val globalStateHolder: AbstractGlobalState? = profile.sharedStateFactory?.invoke()
    var isHeader = false
    var sinceHeader = -1

    context(ChapterHeader)
    var lastDefinedChapter: Chapter
        get() = _lastDefinedChapter
        set(value) {
            _lastDefinedChapter = value
        }

    private var _lastDefinedChapter: Chapter = profile.startChapter

    private lateinit var mlPackage: WordprocessingMLPackage

    private lateinit var comments: CommentsPart

    var totalChildSize: Int = 0
        private set
    var pointer = Pointer()
    private var mistakeId: Long = 0

    fun load(mlPackage: WordprocessingMLPackage) {
        this.mlPackage = mlPackage
        totalChildSize = doc.content.size
        comments = doc.commentsPart ?: CommentsPart().apply {
            jaxbElement = Comments()
            doc.addTargetPart(this)
        }
        mistakeId = comments.jaxbElement.comment.size.toLong()
        profile.sharedStateFactory
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

        val id = BigInteger.valueOf(mistakeId++)
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