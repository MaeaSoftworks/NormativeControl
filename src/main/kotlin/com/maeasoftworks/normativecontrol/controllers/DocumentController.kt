package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.entities.FileResponse
import com.maeasoftworks.normativecontrol.entities.MistakesResponse
import com.maeasoftworks.normativecontrol.entities.StatusResponse
import com.maeasoftworks.normativecontrol.services.DocumentManager
import com.maeasoftworks.normativecontrol.utils.createNullableByteArrayResource
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("document")
@ConditionalOnExpression("\${controllers.api}")
class DocumentController(documentManager: DocumentManager) : ValidatedController(documentManager) {

    @GetMapping("{document-id}/status")
    @ResponseBody
    fun getStatus(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = validate(documentId, accessKey) { StatusResponse(documentId, documentManager.getState(documentId, accessKey)) }

    @GetMapping("{document-id}/mistakes")
    fun getMistakes(
        @PathVariable(value = "document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = validate(documentId, accessKey) { MistakesResponse(documentId, documentManager.getMistakes(documentId)) }

    @GetMapping("{document-id}/raw-file")
    fun getRawFile(
        @PathVariable(value = "document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = validate(documentId, accessKey) { createNullableByteArrayResource(documentManager.getFile(documentId)) }

    @GetMapping("{document-id}/file")
    fun getFile(
        @PathVariable(value = "document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = validate(documentId, accessKey) { FileResponse(documentId, documentManager.getFile(documentId)) }
}