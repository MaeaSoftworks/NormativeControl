package com.maeasoftworks.normativecontrol.dto

import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.docx4nc.enums.Status
import com.maeasoftworks.docx4nc.model.MistakeOuter
import com.maeasoftworks.docxrender.rendering.RenderLauncher
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
            parser.document.data.status = Status.ERROR
            return
        }
        val parsingEnd = System.currentTimeMillis()

        val stream = ByteArrayOutputStream()

        val renderStart = System.currentTimeMillis()
        try {
            RenderLauncher(parser.documentParser).render(stream)
        } catch (e: Exception) {
            parser.document.data.status = Status.RENDER_ERROR
        }

        val renderEnd = System.currentTimeMillis()

        val savingStart = System.currentTimeMillis()
        parser.documentParser.addCommentsAndSave()
        val savingEnd = System.currentTimeMillis()

        parser.render = stream.toString()
        log.info("[{}] total           : {} ms", parser.document.id, savingEnd - parsingStart)
        log.info("[{}] ├─ parsing      : {} ms", parser.document.id, (savingEnd - savingStart) + (parsingEnd - parsingStart))
        log.info("[{}] │  ├─ mistakes  : {} ms", parser.document.id, parsingEnd - parsingStart)
        log.info("[{}] │  └─ saving    : {} ms", parser.document.id, savingEnd - savingStart)
        log.info("[{}] └─ render       : {} ms", parser.document.id, renderEnd - renderStart)
        count.decrementAndGet()
        if (parser.document.data.status != Status.RENDER_ERROR) {
            parser.document.data.status = Status.READY
        }
        afterParsing(parser)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
