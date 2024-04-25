package normativecontrol.core

import normativecontrol.core.chapters.ChapterHeader
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.shared.debug
import normativecontrol.shared.timer
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal class Document(runtime: Runtime) {
    private lateinit var mlPackage: WordprocessingMLPackage
    internal val ctx = VerificationContext(runtime)

    init {
        runtime.context = ctx
    }

    internal fun load(stream: InputStream) {
        timer({ logger.debug { "Unpacking: $it ms" } }) {
            mlPackage = WordprocessingMLPackage.load(stream)
            ctx.load(mlPackage)
        }
    }

    internal fun runVerification() {
        timer({ logger.debug { "Verification: $it ms" } }) {
            with(ctx) {
                doc.content.iterate { pos ->
                    val element = doc.content[pos]
                    val handler = runtime.getHandlerFor(element)
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
    }

    internal fun writeResult(stream: ByteArrayOutputStream) {
        timer({ logger.debug { "Saving: $it ms" } }) {
            mlPackage.save(stream)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Document::class.java)
    }
}