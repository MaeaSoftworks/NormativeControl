package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.State
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException

@CrossOrigin
@RestController
@RequestMapping("documents")
class DocumentProcessingController(private val documentsManager: DocumentManager) {
    @GetMapping("state")
    fun getState(@RequestParam(value = "id") id: String): Map<String, State> {
        val state = documentsManager.getState(id)
        return mapOf("state" to state)
    }

    @PostMapping("upload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun uploadDocument(@RequestParam("file") file: MultipartFile?): Map<String, String> {
        if (file == null || file.originalFilename == null || file.originalFilename == "") {
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
        return mapOf("id" to documentsManager.addToQueue(bytes))
    }

    @GetMapping("errors")
    fun getErrors(@RequestParam(value = "id") id: String): Map<String, List<DocumentError>> {
        val data = documentsManager.getErrors(id)
        return mapOf("errors" to data)
    }

    @GetMapping("file")
    fun getFile(@RequestParam(value = "id") id: String): Resource {
        val file = documentsManager.getFile(id)
        return ByteArrayResource(file)
    }

    @GetMapping("drop-database")
    @ResponseStatus(HttpStatus.OK)
    fun dropDatabase() {
        documentsManager.dropDatabase()
    }
}