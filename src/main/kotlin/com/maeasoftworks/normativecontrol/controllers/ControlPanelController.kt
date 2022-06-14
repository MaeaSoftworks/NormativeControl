package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.dto.response.DocumentControlPanelResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import com.maeasoftworks.normativecontrol.utils.byteArrayResourceOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@CrossOrigin
@RestController
@RequestMapping("control-panel")
@ConditionalOnExpression("\${controllers.api}")
class ControlPanelController(private val documentManager: DocumentManager) {

    @GetMapping("find-by-id")
    @ResponseBody
    fun findById(@RequestParam("document-id") documentId: String): DocumentControlPanelResponse {
        return documentManager.find(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping("delete")
    @ResponseBody
    fun delete(@RequestParam("document-id") documentId: String) {
        documentManager.deleteById(documentId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping("download/{document-id}")
    fun download(@PathVariable("document-id") documentId: String): ResponseEntity<ByteArrayResource?> {
        val headers = HttpHeaders().also {
            it.contentType = MediaType.APPLICATION_OCTET_STREAM
            it.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.docx")
            it.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("$documentId (${LocalDateTime.now()}).docx").build().toString())
        }
        return ResponseEntity.ok().headers(headers).body(byteArrayResourceOrNull(documentManager.getFile(documentId)))
    }
}
