package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.daos.DocumentData
import com.maeasoftworks.normativecontrol.daos.DocumentFile
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.State
import com.maeasoftworks.normativecontrol.events.NewDocumentEvent
import com.maeasoftworks.normativecontrol.repositories.DocumentDataRepository
import com.maeasoftworks.normativecontrol.repositories.DocumentFileRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class DocumentManager(
    private val queue: DocumentQueue,
    private val dataRepository: DocumentDataRepository,
    private val fileRepository: DocumentFileRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun addToQueue(file: ByteArray): String {
        val document = Document(UUID.randomUUID().toString(), file)
        queue.put(document)
        publisher.publishEvent(NewDocumentEvent(this, document.id))
        return document.id
    }

    @Transactional
    fun getState(id: String): State? {
        val document = queue.getById(id)
        return document?.state ?: if (dataRepository.existsById(id)) State.READY else null
    }

    @Transactional
    fun getData(id: String): DocumentData? {
        return dataRepository.findById(id).orElse(null)
    }

    @Transactional
    fun getFile(id: String): DocumentFile? {
        return fileRepository.findById(id).orElse(null)
    }

    @Transactional
    fun dropDatabase() {
        dataRepository.deleteAll()
        fileRepository.deleteAll()
    }
}