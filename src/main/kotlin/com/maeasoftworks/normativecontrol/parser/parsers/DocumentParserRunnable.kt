package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.enums.State
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import java.util.concurrent.atomic.AtomicInteger

class DocumentParserRunnable(
    private var parser: DocumentParser,
    private val count: AtomicInteger,
    private val publisher: ApplicationEventPublisher
) : Runnable {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        val start = System.currentTimeMillis()
        parser.init()
        parser.runVerification()
        val end = System.currentTimeMillis()
        log.info("[{}] time taken: {} ms", parser.document.id, end - start)
        count.decrementAndGet()
        parser.document.state = State.READY
        publisher.publishEvent(parser)
    }
}