package com.maeasoftworks.tellurium.services

import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.polonium.model.DocumentData
import com.maeasoftworks.polonium.parsers.DocumentParser
import com.maeasoftworks.tellurium.dao.MistakesSerializer
import com.maeasoftworks.tellurium.dto.DocumentDTO
import com.maeasoftworks.tellurium.dto.response.DocumentControlPanelResponse
import com.maeasoftworks.tellurium.dto.response.MistakesResponse
import com.maeasoftworks.tellurium.dto.response.QueueResponse
import com.maeasoftworks.tellurium.dto.response.StatusResponse
import com.maeasoftworks.tellurium.repository.DocumentsRepository
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class DocumentManager(
    private val queue: DocumentQueue,
    private val documentsRepository: DocumentsRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val mistakesSerializer: MistakesSerializer
) {
    fun createParser(documentDTO: DocumentDTO) = DocumentParser(documentDTO.data, documentDTO.password)

    fun addToQueue(accessKey: String): QueueResponse {
        val id = UUID.randomUUID().toString().filterNot { it == '-' }
        val documentDTO =
            DocumentDTO(id, accessKey, DocumentData(), UUID.randomUUID().toString().filterNot { it == '-' })
        queue.put(createParser(documentDTO), documentDTO)
        return QueueResponse(id, accessKey)
    }

    fun enqueue(documentId: String, bytes: ByteArray) {
        queue[documentId]!!.documentDTO.data.file = bytes
        queue.run(documentId)
    }

    @Transactional
    fun getState(documentId: String): StatusResponse {
        val order = queue[documentId]
        return StatusResponse(
            documentId = documentId,
            status = if (order?.documentDTO?.data?.status == null) {
                if (documentsRepository.existDocxByDocumentId(documentId)) Status.SAVED else Status.UNDEFINED
            } else if (queue.isUploadAvailable(documentId)) {
                Status.READY_TO_ENQUEUE
            } else {
                order.documentDTO.data.status
            }
        )
    }

    @Transactional
    fun getMistakes(id: String) = MistakesResponse(
        id,
        mistakesSerializer.convertToEntityAttribute(documentsRepository.findMistakesByDocumentId(id)))

    @Transactional
    fun getFile(id: String): ByteArrayResource? {
        return documentsRepository.findDocxByDocumentId(id)?.bytes?.toList()?.toByteArray().let {
            if (it == null) null else ByteArrayResource(it)
        }
    }

    @Transactional
    fun getRender(id: String): String? = documentsRepository.findHtmlByDocumentId(id)?.html

    @Transactional
    fun validateAccessKey(documentId: String, accessKey: String): Boolean {
        return ((queue[documentId]?.documentDTO?.accessKey
            ?: documentsRepository.findCredentialsByDocumentId(documentId)?.accessKey)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Document not found."
            )) == bCryptPasswordEncoder.encode(accessKey)
    }

    fun uploaded(accessKey: String, documentId: String): Boolean {
        return queue[documentId].let {
            it != null && it.documentDTO.accessKey == accessKey && !it.documentDTO.data.file.contentEquals(ByteArray(0))
        } or (documentsRepository.findCredentialsByDocumentId(documentId)?.accessKey == accessKey)
    }

    @Transactional
    fun find(id: String): DocumentControlPanelResponse {
        if (documentsRepository.existsById(id)) {
            val mistakes = mistakesSerializer.convertToEntityAttribute(documentsRepository.findMistakesByDocumentId(id))
            val credentials = documentsRepository.findCredentialsByDocumentId(id)
            return DocumentControlPanelResponse(id, credentials!!.accessKey, credentials.password, mistakes)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun delete(id: String) {
        if (documentsRepository.existsById(id)) {
            documentsRepository.deleteById(id)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
