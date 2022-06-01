package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
abstract class ValidatedController(protected val documentManager: DocumentManager) {
    protected final inline fun <T> validate(documentId: String, accessKey: String, body: () -> T): T {
        val key = documentManager.getAccessKey(documentId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found.")
        if (key != accessKey) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access key is invalid.")
        }
        return body()
    }
}