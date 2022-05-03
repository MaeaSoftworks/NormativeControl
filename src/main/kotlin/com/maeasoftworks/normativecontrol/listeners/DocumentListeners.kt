package com.maeasoftworks.normativecontrol.listeners

import com.maeasoftworks.normativecontrol.daos.DocumentChunk
import com.maeasoftworks.normativecontrol.dtos.State
import com.maeasoftworks.normativecontrol.events.NewDocumentEvent
import com.maeasoftworks.normativecontrol.events.SaveDocumentEvent
import com.maeasoftworks.normativecontrol.repositories.DocumentErrorRepository
import com.maeasoftworks.normativecontrol.repositories.DocumentChunkRepository
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
    private val errorRepository: DocumentErrorRepository,
    private val fileRepository: DocumentChunkRepository
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
        val document = storage.getById(event.documentId)
        if (document!!.result == null || document.result!!.isFail) {
            return
        }
        errorRepository.saveAll(document.result!!.errors)
        val chunks = ArrayList<DocumentChunk>()
        var pointer = 0
        var chunk = ArrayList<Byte>()
        var chunkId = 0
        for (byteId in document.file.indices) {
            if (pointer == 1024 * 1024) {
                pointer = 0
                chunks.add(DocumentChunk(event.documentId, chunkId.toLong(), chunk.toByteArray()))
                chunk = ArrayList()
                chunkId++
            }
            chunk.add(document.file[byteId])
            pointer++
        }
        chunks.add(DocumentChunk(event.documentId, chunkId.toLong(), chunk.toByteArray()))
        for (fileChunk in chunks) {
            fileRepository.save(fileChunk)
        }
        storage.removeAt(document.id)
    }
}