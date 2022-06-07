package com.maeasoftworks.normativecontrol.dto

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import java.util.concurrent.atomic.AtomicInteger

class DocumentParserRunnable(
    private var order: OrderedParser,
    private val count: AtomicInteger,
    private val publisher: ApplicationEventPublisher
) : Runnable {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        val start = System.currentTimeMillis()
        order.documentParser.init()
        order.documentParser.runVerification()
        val end = System.currentTimeMillis()
        log.info("[{}] time taken: {} ms", order.document.id, end - start)
        count.decrementAndGet()
        order.document.data.status = Status.READY
        publisher.publishEvent(order)
    }
}
