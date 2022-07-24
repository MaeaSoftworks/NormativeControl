package com.maeasoftworks.tellurium.dto

import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.livermorium.rendering.RenderLauncher
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
            parser.documentParser.init()
            parser.documentParser.runVerification()
        } catch (e: Exception) {
            parser.documentDTO.data.status = Status.ERROR
            log.error("Oops!", e)
            return
        }
        val parsingEnd = System.currentTimeMillis()

        val stream = ByteArrayOutputStream()

        val renderStart = System.currentTimeMillis()
        try {
            RenderLauncher(parser.documentParser).render(stream)
        } catch (e: Exception) {
            parser.documentDTO.data.status = Status.RENDER_ERROR
            log.error("Wow!", e)
        }

        val renderEnd = System.currentTimeMillis()

        val savingStart = System.currentTimeMillis()
        parser.documentParser.addCommentsAndSave()
        val savingEnd = System.currentTimeMillis()

        parser.render = stream.toString()
        log.info("[{}] total           : {} ms", parser.documentDTO.id, savingEnd - parsingStart)
        log.info("[{}] ├─ parsing      : {} ms",
            parser.documentDTO.id,
            (savingEnd - savingStart) + (parsingEnd - parsingStart)
        )
        log.info("[{}] │  ├─ mistakes  : {} ms", parser.documentDTO.id, parsingEnd - parsingStart)
        log.info("[{}] │  └─ saving    : {} ms", parser.documentDTO.id, savingEnd - savingStart)
        log.info("[{}] └─ render       : {} ms", parser.documentDTO.id, renderEnd - renderStart)
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
