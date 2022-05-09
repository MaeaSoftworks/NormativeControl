package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.enums.State
import org.springframework.context.ApplicationEventPublisher

class DocumentParserRunnable(
    private var parser: DocumentParser,
    private val count: IntArray,
    private val publisher: ApplicationEventPublisher
) : Runnable {
    override fun run() {
        parser.init()
        parser.runVerification()
        synchronized(count) {
            count[0]--
        }
        parser.document.state = State.READY
        publisher.publishEvent(parser)
    }
}