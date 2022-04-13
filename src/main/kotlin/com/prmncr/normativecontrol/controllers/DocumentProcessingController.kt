package com.prmncr.normativecontrol.controllers

import com.fasterxml.jackson.core.JsonProcessingException
import com.prmncr.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@CrossOrigin
@RestController
@RequestMapping("api/documents")
class DocumentProcessingController(private var documentsManager: DocumentManager) {
    @GetMapping("state")
    @ResponseBody
    fun getState(@RequestParam(value = "id") id: String): ResponseEntity<Any> {
        val s = documentsManager.getState(id)
        return if (s != null) ResponseEntity(object : Any() {
            val state = s
        }, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("result")
    @ResponseBody
    fun getResult(@RequestParam(value = "id") id: String): ResponseEntity<Any> {
        val r = documentsManager.getResult(id)
        return if (r != null) ResponseEntity(object : Any() {
            val result = r
        }, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PostMapping("upload")
    @ResponseBody
    fun uploadDocument(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        val filename = file.originalFilename
        if (filename == null || filename == "") {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        val extension = filename.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (extension[1] != "docx") {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        val bytes: ByteArray = try {
            file.bytes
        } catch (e: IOException) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        val documentId = documentsManager.addToQueue(bytes)
        return ResponseEntity(object : Any() {
            val id = documentId
        }, HttpStatus.ACCEPTED)
    }

    @GetMapping("result/{id}")
    @ResponseBody
    fun loadResult(@PathVariable(value = "id") id: String): ResponseEntity<Any> {
        var file: Any? = null
        try {
            file = documentsManager.getFile(id)
        } catch (e: JsonProcessingException) {
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return if (file != null) ResponseEntity(file, HttpStatus.OK) else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PatchMapping("result")
    @ResponseBody
    fun saveResult(@RequestParam(value = "id") id: String): ResponseEntity<String> {
        try {
            documentsManager.saveResult(id)
        } catch (e: NullPointerException) {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: JsonProcessingException) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @DeleteMapping("result")
    @ResponseBody
    fun deleteResult(@RequestParam(value = "id") id: String): ResponseEntity<String> {
        documentsManager.delete(id)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("drop-database")
    @ResponseBody
    fun dropDatabase(): ResponseEntity<String> {
        documentsManager.dropDatabase()
        return ResponseEntity(HttpStatus.OK)
    }
}