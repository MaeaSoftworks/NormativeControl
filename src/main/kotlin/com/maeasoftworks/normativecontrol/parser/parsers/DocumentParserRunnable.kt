package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.enums.State
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher

class DocumentParserRunnable(
    private var parser: DocumentParser,
    private val count: IntArray,
    private val publisher: ApplicationEventPublisher
) : Runnable {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        val start = System.currentTimeMillis()
        parser.init()
        parser.runVerification()
        val end = System.currentTimeMillis()
        log.info("[{}] time taken: {} ms", parser.document.id, end - start)
        synchronized(count) {
            count[0]--
        }
        parser.document.state = State.READY
        publisher.publishEvent(parser)
    }
}