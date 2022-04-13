package com.prmncr.normativecontrol.listeners

import com.prmncr.normativecontrol.dtos.State
import com.prmncr.normativecontrol.events.NewDocumentEvent
import com.prmncr.normativecontrol.services.DocumentHandler
import com.prmncr.normativecontrol.services.DocumentQueue
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NewDocumentListener(private val storage: DocumentQueue,
                          private val handler: DocumentHandler) {
    @Async
    @EventListener
    fun handleDocument(event: NewDocumentEvent) {
        val document = storage.getById(event.documentId)!!
        document.state = State.PROCESSING
        handler.handle(document)
        document.state = State.READY
    }
}