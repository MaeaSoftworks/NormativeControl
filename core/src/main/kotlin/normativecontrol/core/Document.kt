package normativecontrol.core

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.utils.timer
import normativecontrol.shared.debug
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Document(configuration: Configuration) {
    private lateinit var mlPackage: WordprocessingMLPackage
    val ctx = VerificationContext(configuration)

    fun load(stream: InputStream) {
        timer({ logger.debug { "Unpacking: $it ms" } }) {
            mlPackage = WordprocessingMLPackage.load(stream)
            ctx.load(mlPackage)
        }
    }

    fun runVerification() = timer({ logger.debug { "Verification: $it ms" } }) {
        with(ctx) {
            doc.content.iterate { pos ->
                val element = doc.content[pos]
                val handler = HandlerMapper[configuration, element]
                if (handler != null) {
                    if (handler is ChapterHeader) {
                        val chapter = handler.checkChapterStart(element)
                        if (chapter != null) {
                            //logger.debug { "Header '${TextUtils.getText(element)}' - $chapter" }
                            isHeader = true
                            sinceHeader = 0
                            handler.checkChapterOrderAndUpdateContext(chapter)
                        } else {
                            isHeader = false
                            sinceHeader++
                        }
                    }
                    handler.handle(element)
                    handler.nullableState?.reset()
                }
            }
        }
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        timer({ logger.debug { "Saving: $it ms" } }) {
            mlPackage.save(stream)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Document::class.java)

        init {
            Loader
        }
    }
}