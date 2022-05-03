package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.State
import com.maeasoftworks.normativecontrol.events.NewDocumentEvent
import com.maeasoftworks.normativecontrol.repositories.DocumentErrorRepository
import com.maeasoftworks.normativecontrol.repositories.DocumentChunkRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class DocumentManager(
    private val queue: DocumentQueue,
    private val errorRepository: DocumentErrorRepository,
    private val fileRepository: DocumentChunkRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun addToQueue(file: ByteArray): String {
        val document = Document(UUID.randomUUID().toString(), file)
        queue.put(document)
        publisher.publishEvent(NewDocumentEvent(this, document.id))
        return document.id
    }

    @Transactional
    fun getState(id: String): State {
        val document = queue.getById(id)
        if (document?.state == null) {
            if (fileRepository.existsDocumentChunkByDocumentId(id)) {
                return State.READY
            } else {
                return State.UNDEFINED
            }

        }
        return State.UNDEFINED
    }

    @Transactional
    fun getErrors(id: String): List<DocumentError> {
        return errorRepository.findAllByDocumentId(id)
    }

    @Transactional
    fun getFile(id: String): ByteArray {
        return fileRepository.findAllByDocumentIdOrderByChunkId(id).flatMap { it.file.toList() }.toByteArray()
    }

    @Transactional
    fun dropDatabase() {
        errorRepository.deleteAll()
        fileRepository.deleteAll()
    }
}