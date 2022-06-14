package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class DocumentCredentialsConfirmedController(protected val documentManager: DocumentManager) {
    protected inline fun <T> confirming(documentId: String, accessKey: String, body: DocumentManager.() -> T): T {
        if (documentManager.getAccessKey(documentId) != accessKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body(documentManager)
    }
}
