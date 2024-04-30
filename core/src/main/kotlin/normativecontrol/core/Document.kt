package normativecontrol.core

import normativecontrol.core.chapters.ChapterHeader
import normativecontrol.core.contexts.VerificationContext
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal class Document(runtime: Runtime, file: InputStream) {
    val mistakeCount: Int
        get() = ctx.lastMistakeId.toInt()

    val render: String
        get() = ctx.render.render()

    private val mlPackage: WordprocessingMLPackage = WordprocessingMLPackage.load(file)
    private val ctx = VerificationContext(runtime, mlPackage)

    init {
        runtime.context = ctx
    }

    internal fun runVerification() {
        with(ctx) {
            doc.content.iterate { element, _ ->
                val handler = runtime.handlers[element]
                if (handler != null) {
                    if (handler is ChapterHeader) {
                        val chapter = handler.checkChapterStart(element)
                        if (chapter != null) {
                            handler.checkChapterOrder(chapter)
                        }
                    }
                    handler.handleElement(element)
                }
            }
        }
    }

    internal fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }
}