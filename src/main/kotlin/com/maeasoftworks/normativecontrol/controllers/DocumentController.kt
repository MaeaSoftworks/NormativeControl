package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.dto.response.FileResponse
import com.maeasoftworks.normativecontrol.dto.response.MistakesResponse
import com.maeasoftworks.normativecontrol.dto.response.StatusResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import com.maeasoftworks.normativecontrol.utils.byteArrayResourceOrNull
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@CrossOrigin
@RestController
@RequestMapping("document")
@ConditionalOnExpression("\${controllers.api}")
class DocumentController(documentManager: DocumentManager) : DocumentCredentialsConfirmedController(documentManager) {

    @GetMapping("{document-id}/status")
    @ResponseBody
    fun getStatus(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirming(documentId, accessKey) { StatusResponse(documentId, documentManager.getState(documentId, accessKey)) }

    @GetMapping("{document-id}/mistakes")
    fun getMistakes(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirming(documentId, accessKey) { MistakesResponse(documentId, documentManager.getMistakes(documentId)) }

    @GetMapping("{document-id}/raw-file")
    fun getRawFile(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirming(documentId, accessKey) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.docx")
        headers.set(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename("$documentId (${LocalDateTime.now()}).docx").build().toString()
        )
        return@confirming ResponseEntity.ok().headers(headers)
            .body(byteArrayResourceOrNull(documentManager.getFile(documentId)))
    }

    @GetMapping("{document-id}/file")
    fun getFile(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirming(documentId, accessKey) { FileResponse(documentId, documentManager.getFile(documentId)) }
}
