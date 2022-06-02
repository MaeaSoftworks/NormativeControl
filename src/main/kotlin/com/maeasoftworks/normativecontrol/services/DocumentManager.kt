package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.controllers.DocumentController
import com.maeasoftworks.normativecontrol.entities.BinaryFile
import com.maeasoftworks.normativecontrol.entities.DocumentCredentials
import com.maeasoftworks.normativecontrol.parser.enums.FailureType
import com.maeasoftworks.normativecontrol.parser.enums.Status
import com.maeasoftworks.normativecontrol.parser.model.Document
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParserFactory
import com.maeasoftworks.normativecontrol.repositories.BinaryFileRepository
import com.maeasoftworks.normativecontrol.repositories.CredentialsRepository
import com.maeasoftworks.normativecontrol.repositories.MistakeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(DocumentController::class)
class DocumentManager @Autowired constructor(
    private val queue: DocumentQueue,
    private val errorRepository: MistakeRepository,
    private val fileRepository: BinaryFileRepository,
    private val credentialsRepository: CredentialsRepository,
    private val factory: DocumentParserFactory
) {
    fun addToQueue(accessKey: String): String {
        val id = UUID.randomUUID().toString().filterNot { it == '-' }
        queue.put(factory.create(Document(id, accessKey)))
        return id
    }

    fun appendFile(documentId: String, accessKey: String, bytes: ByteArray) {
        val parser = queue.getById(documentId)
        if (parser?.document?.accessKey == accessKey) {
            parser.document.file = bytes
            queue.runById(documentId)
        } else {
            TODO("throw error")
        }
    }

    @EventListener
    @Async
    fun saveToDatabase(parser: DocumentParser) {
        if (parser.document.failureType == FailureType.NONE) {
            fileRepository.save(BinaryFile(parser.document.id, parser.document.accessKey, parser.document.file))
            errorRepository.saveAll(parser.errors)
            credentialsRepository.save(DocumentCredentials(parser.document.id, parser.document.accessKey))
            queue.remove(parser.document.id)
        }
    }

    @Transactional
    fun getState(documentId: String, accessKey: String): Status {
        val parser = queue.getById(documentId)
        return if (parser?.document?.status == null) {
            if (fileRepository.existsBinaryFileByDocumentId(documentId)) Status.SAVED else Status.UNDEFINED
        } else if (queue.isUploadAvailable(documentId)) {
            Status.READY_TO_ENQUEUE
        } else {
            parser.document.status
        }
    }

    @Transactional
    fun getMistakes(id: String) = errorRepository.findAllByDocumentId(id)

    @Transactional
    fun getFile(id: String) = fileRepository.findByDocumentId(id)?.bytes?.toList()?.toByteArray()

    @Transactional
    fun dropDatabase() {
        errorRepository.deleteAll()
        fileRepository.deleteAll()
        credentialsRepository.deleteAll()
    }

    @Transactional
    fun getAccessKey(documentId: String): String? {
        return queue.getById(documentId)?.document?.accessKey ?: credentialsRepository.findById(documentId).orElse(
            DocumentCredentials("", null)
        ).accessKey
    }

    fun uploaded(accessKey: String, documentId: String): Boolean {
        return queue.getById(documentId)
            .let { it != null && it.document.accessKey == accessKey && !it.document.file.contentEquals(ByteArray(0)) } || credentialsRepository.findById(
            documentId
        ).let { it.isPresent && it.get().accessKey == accessKey }
    }
}