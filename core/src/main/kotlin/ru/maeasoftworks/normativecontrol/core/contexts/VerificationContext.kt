package ru.maeasoftworks.normativecontrol.core.contexts

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.utils.PropertyResolver
import java.math.BigInteger
import java.util.*

class VerificationContext(val profile: Profile) {
    val resolver: PropertyResolver by lazy { PropertyResolver(mlPackage) }
    val render: RenderingContext by lazy { RenderingContext(doc) }
    var chapter: Chapter = profile.startChapter
    val doc: MainDocumentPart by lazy { mlPackage.mainDocumentPart }
    val states = mutableMapOf<State.Key, State>()
    var mistakeUid: String? = null
    val sharedState: AbstractSharedState? = profile.sharedState?.invoke()

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
    var bodyPosition = 0
        private set
    private var totalChildContentSize: Int = 0
    private var childContentPosition = 0
    private var mistakeId: Long = 0


    fun load(mlPackage: WordprocessingMLPackage) {
        this.mlPackage = mlPackage
        totalChildSize = doc.content.size
        comments = doc.commentsPart ?: CommentsPart().apply {
            jaxbElement = Comments()
            doc.addTargetPart(this)
        }
        mistakeId = comments.jaxbElement.comment.size.toLong()
        profile.sharedState
    }

    inline fun <reified T : AbstractSharedState> getSharedStateAs(): T {
        return sharedState as? T ?: throw NullPointerException("This profile does not have shared state.")
    }

    fun mainLoop(fn: (pos: Int) -> Unit) {
        while (bodyPosition < totalChildSize) {
            fn(bodyPosition)
            bodyPosition++
        }
    }

    fun childLoop(fn: (pos: Int) -> Unit) {
        totalChildContentSize = (doc.content[bodyPosition] as ContentAccessor).content.size
        while (childContentPosition < totalChildContentSize) {
            fn(childContentPosition)
            childContentPosition++
        }
        childContentPosition = 0
    }

    fun addMistake(mistake: Mistake) {
        val formattedText = if (mistake.actual != null && mistake.expected != null) {
            "${mistake.mistakeReason.description}: найдено: ${mistake.actual}, требуется: ${mistake.expected}."
        } else {
            mistake.mistakeReason.description
        }

        val id = mistakeId++
        mistakeUid = "m$id"

        render.mistakeRenderer.addMistake(
            mistake.mistakeReason,
            mistakeUid!!,
            mistake.expected,
            mistake.actual
        )

        val comment = createComment(id, formattedText)
        comments.jaxbElement.comment.add(comment)

        val commentRangeStart = CommentRangeStart().apply {
            this.id = BigInteger.valueOf(id)
        }
        val commentRangeEnd = CommentRangeEnd().apply {
            this.id = BigInteger.valueOf(id)
        }

        when (mistake.closure) {
            Closure.SECTOR -> {
                val paragraph = doc.content[bodyPosition] as P
                paragraph.content.add(0, commentRangeStart)
                paragraph.content.add(1, commentRangeEnd)
                paragraph.content += createRunCommentReference(id)
                totalChildContentSize += 3
                childContentPosition += 3
            }

            Closure.P -> {
                var paragraphStart: P
                var putInStartToStart = false
                var paragraphEnd: P
                var putInEndToEnd = false

                try {
                    paragraphStart = doc.content[bodyPosition] as P
                    putInStartToStart = true
                } catch (e: ClassCastException) {
                    paragraphStart = doc.content[bodyPosition - 1] as P
                }

                try {
                    paragraphEnd = doc.content[bodyPosition] as P
                    putInEndToEnd = true
                } catch (e: ClassCastException) {
                    paragraphEnd = doc.content[bodyPosition + 1] as P
                }

                if (putInStartToStart) {
                    paragraphStart.content.add(0, commentRangeStart)
                    paragraphStart.content += createRunCommentReference(id)
                    totalChildContentSize += 2
                } else {
                    paragraphStart.content.add(commentRangeStart)
                    paragraphStart.content += createRunCommentReference(id)
                }

                if (putInEndToEnd) {
                    paragraphEnd.content.add(commentRangeEnd)
                    totalChildContentSize++
                } else {
                    paragraphEnd.content.add(0, commentRangeEnd)
                }
            }

            Closure.R -> {
                val paragraph: P = try {
                    doc.content[bodyPosition] as P
                } catch (e: ClassCastException) {
                    totalChildSize++
                    P().apply { doc.content.add(bodyPosition + 1, this) }
                }
                paragraph.content.add(if (childContentPosition == 0) 0 else childContentPosition - 1, commentRangeStart)
                childContentPosition++
                totalChildContentSize++
                if (childContentPosition == totalChildContentSize - 1) {
                    paragraph.content += commentRangeEnd
                    paragraph.content += createRunCommentReference(id)
                    childContentPosition += 2
                    totalChildContentSize += 2
                } else {
                    paragraph.content.add(childContentPosition + 1, commentRangeEnd)
                    paragraph.content.add(childContentPosition + 2, createRunCommentReference(id))
                    childContentPosition += 2
                    totalChildContentSize += 2
                }
            }
        }
    }

    private fun createComment(commentId: Long, message: String): Comments.Comment {
        return Comments.Comment().apply {
            id = BigInteger.valueOf(commentId)
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

    private fun createRunCommentReference(commentId: Long): R {
        return R().also { run ->
            run.content.add(
                R.CommentReference().also {
                    it.id = BigInteger.valueOf(commentId)
                }
            )
        }
    }
}