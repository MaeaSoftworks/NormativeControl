package com.maeasoftworks.normativecontrol.dto

import com.maeasoftworks.docx4nc.enums.Status
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class DocumentParserRunnable(
    private var parser: EnqueuedParser,
    private val count: AtomicInteger,
    private val afterParsing: (EnqueuedParser) -> Unit
) : Runnable {

    override fun run() {
        val start = System.currentTimeMillis()
        parser.documentParser.init()
        parser.documentParser.runVerification()
        val end = System.currentTimeMillis()
        log.info("[{}] time taken: {} ms", parser.document.id, end - start)
        count.decrementAndGet()
        parser.document.data.status = Status.READY
        afterParsing(parser)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
