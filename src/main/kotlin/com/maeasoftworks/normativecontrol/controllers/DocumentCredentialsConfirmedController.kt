package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class DocumentCredentialsConfirmedController(protected val documentManager: DocumentManager) {
    protected inline fun <T> confirming(documentId: String, accessKey: String, body: () -> T): T {
        val key = documentManager.getAccessKey(documentId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found.")
        if (key != accessKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body()
    }
}
