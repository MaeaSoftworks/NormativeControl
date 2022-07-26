package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.tellurium.services.DocumentManager
import com.maeasoftworks.tellurium.utils.toResponse
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
@RequestMapping("document")
class DocumentController(documentManager: DocumentManager) : DocumentCredentialsConfirmedController(documentManager) {

    @GetMapping("{document-id}/status")
    @ResponseBody
    fun getStatus(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirm(documentId, accessKey) { getState(documentId) }

    @GetMapping("{document-id}/mistakes")
    fun getMistakes(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirm(documentId, accessKey) { getMistakes(documentId) }

    @GetMapping("{document-id}/raw-file")
    fun getRawFile(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirm(documentId, accessKey) { getFile(documentId).toResponse(documentId) }

    @GetMapping("{document-id}/render")
    fun getRender(
        @PathVariable("document-id") documentId: String,
        @RequestParam("access-key") accessKey: String
    ) = confirm(documentId, accessKey) { getRender(documentId) }
}
