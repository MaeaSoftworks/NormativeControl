package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.docx4nc.model.DocumentData
import com.maeasoftworks.docx4nc.model.FailureType
import com.maeasoftworks.normativecontrol.controllers.DocumentController
import com.maeasoftworks.normativecontrol.dao.BinaryFile
import com.maeasoftworks.normativecontrol.dao.DocumentCredentials
import com.maeasoftworks.normativecontrol.dto.Document
import com.maeasoftworks.normativecontrol.dto.OrderedParser
import com.maeasoftworks.normativecontrol.dto.Status
import com.maeasoftworks.normativecontrol.repository.BinaryFileRepository
import com.maeasoftworks.normativecontrol.repository.CredentialsRepository
import com.maeasoftworks.normativecontrol.repository.MistakeRepository
import com.maeasoftworks.normativecontrol.utils.toDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(DocumentController::class)
class DocumentManager(
    private val queue: DocumentQueue,
    private val errorRepository: MistakeRepository,
    private val fileRepository: BinaryFileRepository,
    private val credentialsRepository: CredentialsRepository,
    private val factory: DocumentParserFactory
) {
    fun addToQueue(accessKey: String): String {
        val id = UUID.randomUUID().toString().filterNot { it == '-' }
        val document = Document(id, accessKey, DocumentData())
        queue.put(factory.create(document), document)
        return id
    }

    fun appendFile(documentId: String, accessKey: String, bytes: ByteArray) {
        val order = queue.getById(documentId)
        if (order?.document?.accessKey == accessKey) {
            order.document.data.file = bytes
            queue.runById(documentId)
        } else {
            TODO("throw error")
        }
    }

    @EventListener
    @Async
    fun saveToDatabase(order: OrderedParser) {
        if (order.document.data.failureType == FailureType.NONE) {
            fileRepository.save(
                BinaryFile(
                    order.document.id,
                    order.document.data.file,
                    UUID.randomUUID().toString().filterNot { it == '-' }
                )
            )
            errorRepository.saveAll(order.documentParser.mistakes.map { it.toDto(order.document.id) })
            credentialsRepository.save(DocumentCredentials(order.document.id, order.document.accessKey))
            queue.remove(order.document.id)
        }
    }

    @Transactional
    fun getState(documentId: String, accessKey: String): Status {
        val order = queue.getById(documentId)
        return if (order?.document?.data?.status == null) {
            if (fileRepository.existsBinaryFileByDocumentId(documentId)) Status.SAVED else Status.UNDEFINED
        } else if (queue.isUploadAvailable(documentId)) {
            Status.READY_TO_ENQUEUE
        } else {
            order.document.data.status
        }
    }

    @Transactional
    fun getMistakes(id: String) = errorRepository.findAllByDocumentId(id)

    @Transactional
    fun getFile(id: String) = fileRepository.findByDocumentId(id)?.bytes?.toList()?.toByteArray()

    @Transactional
    fun getAccessKey(documentId: String): String? {
        return queue.getById(documentId)?.document?.accessKey ?: credentialsRepository.findById(documentId).orElse(
            DocumentCredentials("", null)
        ).accessKey
    }

    fun uploaded(accessKey: String, documentId: String): Boolean {
        return queue.getById(documentId)
            .let {
                it != null && it.document.accessKey == accessKey &&
                    !it.document.data.file.contentEquals(ByteArray(0))
            } ||
            credentialsRepository.findById(documentId)
                .let { it.isPresent && it.get().accessKey == accessKey }
    }

    fun find(filter: String): List<BinaryFile>? {
        return fileRepository.findByDocumentId(filter).let { if (it != null) listOf(it) else null }
    }

    fun findAll(): List<BinaryFile> {
        return fileRepository.findAll()
    }
}
