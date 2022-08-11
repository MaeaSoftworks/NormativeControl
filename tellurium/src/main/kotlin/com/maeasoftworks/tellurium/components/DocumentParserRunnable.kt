package com.maeasoftworks.tellurium.components

import com.maeasoftworks.livermorium.rendering.RenderLauncher
import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.tellurium.dto.EnqueuedParser
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicInteger

class DocumentParserRunnable(
    private var parser: EnqueuedParser,
    private val count: AtomicInteger,
    private val afterParsing: (EnqueuedParser) -> Unit
) : Runnable {

    override fun run() {
        val parsingStart = System.currentTimeMillis()
        try {
            parser.documentParser.runVerification()
        } catch (e: Exception) {
            parser.documentDTO.data.status = Status.ERROR
            log.error("Polonium! What are you doing? / Parsing error.", e)
            return
        }
        val parsingEnd = System.currentTimeMillis()

        val stream = ByteArrayOutputStream()

        val renderStart = System.currentTimeMillis()
        try {
            RenderLauncher(parser.documentParser).render(stream)
        } catch (e: Exception) {
            parser.documentDTO.data.status = Status.RENDER_ERROR
            log.error("Livermorium was disintegrated! / Rendering error.", e)
        }

        val renderEnd = System.currentTimeMillis()

        val savingStart = System.currentTimeMillis()
        parser.documentParser.addCommentsAndSave()
        val savingEnd = System.currentTimeMillis()

        parser.render = stream.toString()
        log.info("[{}] total           : {} ms", parser.documentDTO.id, savingEnd - parsingStart)
        log.info(
            "[{}] ├─ parsing      : {} ms",
            parser.documentDTO.id,
            (savingEnd - savingStart) + (parsingEnd - parsingStart)
        )
        log.info("[{}] │  ├─ checking  : {} ms", parser.documentDTO.id, parsingEnd - parsingStart)
        log.info("[{}] │  └─ saving    : {} ms", parser.documentDTO.id, savingEnd - savingStart)
        log.info("[{}] └─ rendering    : {} ms", parser.documentDTO.id, renderEnd - renderStart)
        count.decrementAndGet()
        if (parser.documentDTO.data.status != Status.RENDER_ERROR) {
            parser.documentDTO.data.status = Status.READY
        }
        afterParsing(parser)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
