package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

@CrossOrigin
@RestController
@RequestMapping("queue")
class QueueController(documentManager: DocumentManager) : DocumentCredentialsConfirmedController(documentManager) {

    @PostMapping("reserve")
    @ResponseBody
    fun reserve(@RequestParam("access-key") accessKey: String) = documentManager.addToQueue(accessKey)

    @PostMapping("enqueue")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun enqueue(
        @RequestParam("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String,
        @RequestParam("file") file: MultipartFile
    ) = confirm(documentId, accessKey) {
        if (uploaded(accessKey, documentId)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "File already uploaded")
        }
        if (file.originalFilename == null || file.originalFilename == "") {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Required arguments were empty")
        }
        if (file.originalFilename!!.split(".")[1] != "docx") {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot process this document")
        }
        val bytes: ByteArray = try {
            file.bytes
        } catch (e: IOException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot process this document")
        }
        enqueue(documentId, bytes)
    }
}
