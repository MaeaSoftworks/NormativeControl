package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.documentation.DocumentProcessingDocumentation
import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.State
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

@CrossOrigin
@RestController
@RequestMapping("documents")
@ConditionalOnExpression("\${controllers.api}")
class DocumentProcessingController(private val documentManager: DocumentManager) : DocumentProcessingDocumentation {
    @PostMapping("queue")
    override fun addToQueue(@RequestParam("accessKey") accessKey: String): Map<String, String> {
        return mapOf("documentId" to documentManager.addToQueue(accessKey))
    }

    @GetMapping("state")
    override fun getState(
        @RequestParam("documentId") documentId: String,
        @RequestParam("accessKey") accessKey: String
    ): Map<String, State> =
        mapOf("state" to validate(documentId, accessKey) { documentManager.getState(documentId, accessKey) })

    @PostMapping("upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    override fun uploadDocument(
        @RequestParam("documentId") documentId: String,
        @RequestParam("accessKey") accessKey: String,
        @RequestParam("file") file: MultipartFile
    ) {
        if (documentManager.uploaded(accessKey, documentId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File already uploaded")
        }
        if (file.originalFilename == null || file.originalFilename == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Required arguments were empty")
        }
        val extension = file.originalFilename!!.split(".")
        if (extension[1] != "docx") {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot process this document")
        }
        val bytes: ByteArray = try {
            file.bytes
        } catch (e: IOException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Can not process this document")
        }
        validate(documentId, accessKey) { documentManager.appendFile(documentId, accessKey, bytes) }
    }

    @GetMapping("errors")
    override fun getErrors(
        @RequestParam(value = "documentId") documentId: String,
        @RequestParam("accessKey") accessKey: String
    ): Map<String, List<DocumentError>> =
        mapOf("errors" to validate(documentId, accessKey) { documentManager.getErrors(documentId) })

    @GetMapping("file")
    override fun getFile(
        @RequestParam(value = "data") data: String
    ): ByteArrayResource? {
        val dataArray = data.split('_')
        val documentId = dataArray[0]
        val accessKey = dataArray[1]
        return validate(
            documentId,
            accessKey
        ) { documentManager.getFile(documentId) }.let { if (it == null) null else ByteArrayResource(it) }
    }

    @GetMapping("drop-database")
    @ResponseStatus(HttpStatus.OK)
    override fun dropDatabase() = documentManager.dropDatabase()

    private inline fun <T> validate(documentId: String, accessKey: String, body: () -> T): T {
        val key = documentManager.getAccessKey(documentId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found.")
        if (key != accessKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body()
    }
}