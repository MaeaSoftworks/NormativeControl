package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.documentation.DocumentProcessingDocumentation
import com.maeasoftworks.normativecontrol.dtos.enums.State
import com.maeasoftworks.normativecontrol.services.DocumentManager
import com.maeasoftworks.normativecontrol.services.DocumentQueue
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

@CrossOrigin
@RestController
@RequestMapping("documents")
class DocumentProcessingController(
    private val documentManager: DocumentManager,
    private val queue: DocumentQueue
) : DocumentProcessingDocumentation {

    @PostMapping("queue")
    override fun addToQueue(@RequestParam("accessKey") accessKey: String): Map<String, String> {
        return mapOf("documentId" to documentManager.addToQueue(accessKey))
    }

    @GetMapping("state")
    override fun getState(@RequestParam("documentId") documentId: String,
                          @RequestParam("accessKey") accessKey: String
    ): Map<String, State> {
        return mapOf("state" to validate(documentId, accessKey) { documentManager.getState(documentId, accessKey) })
    }

    @PostMapping("upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    override fun uploadDocument(@RequestParam("documentId") documentId: String,
                                @RequestParam("accessKey") accessKey: String,
                                @RequestParam("file") file: MultipartFile
    ) {
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
    override fun getErrors(@RequestParam(value = "documentId") documentId: String,
                           @RequestParam("accessKey") accessKey: String
    ): Map<String, List<DocumentError>> {
        val data = validate(documentId, accessKey) { documentManager.getErrors(documentId) }
        return mapOf("errors" to data)
    }

    @GetMapping("file")
    override fun getFile(@RequestParam(value = "documentId") documentId: String,
                         @RequestParam("accessKey") accessKey: String
    ): ByteArrayResource {
        val file = validate(documentId, accessKey) { documentManager.getFile(documentId) }
        return ByteArrayResource(file)
    }

    @GetMapping("drop-database")
    @ResponseStatus(HttpStatus.OK)
    override fun dropDatabase() {
        documentManager.dropDatabase()
    }

    private inline fun <T> validate(documentId: String, accessKey: String, body: () -> T): T {
        val parser = queue.getById(documentId)
        if (parser?.document?.accessKey == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found.")
        }
        if (parser.document.accessKey != accessKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body()
    }
}