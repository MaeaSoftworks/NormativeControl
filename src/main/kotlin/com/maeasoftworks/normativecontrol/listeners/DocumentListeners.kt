package com.maeasoftworks.normativecontrol.listeners

import com.maeasoftworks.normativecontrol.daos.DocumentData
import com.maeasoftworks.normativecontrol.daos.DocumentFile
import com.maeasoftworks.normativecontrol.dtos.State
import com.maeasoftworks.normativecontrol.events.NewDocumentEvent
import com.maeasoftworks.normativecontrol.events.SaveDocumentEvent
import com.maeasoftworks.normativecontrol.repositories.DocumentDataRepository
import com.maeasoftworks.normativecontrol.repositories.DocumentFileRepository
import com.maeasoftworks.normativecontrol.services.DocumentHandler
import com.maeasoftworks.normativecontrol.services.DocumentQueue
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DocumentListeners(
    private val storage: DocumentQueue,
    private val handler: DocumentHandler,
    private val publisher: ApplicationEventPublisher,
    private val dataRepository: DocumentDataRepository,
    private val fileRepository: DocumentFileRepository
) {
    @Async
    @EventListener
    fun handleDocument(event: NewDocumentEvent) {
        val document = storage.getById(event.documentId)!!
        document.state = State.PROCESSING
        handler.handle(document)
        publisher.publishEvent(SaveDocumentEvent(this, document.id))
    }

    @Async
    @EventListener
    @Transactional
    fun saveDocument(event: SaveDocumentEvent) {
        val document = storage.getById(event.documentId)!!
        dataRepository.save(DocumentData(document.id, document.result?.errors))
        fileRepository.save(DocumentFile(document.id, document.file))
        storage.removeAt(document.id)
    }
}