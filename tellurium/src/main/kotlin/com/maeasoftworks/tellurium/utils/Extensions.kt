package com.maeasoftworks.tellurium.utils

import com.maeasoftworks.polonium.model.MistakeOuter
import com.maeasoftworks.tellurium.dao.Mistake
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

fun MistakeOuter.toDao(documentId: String): Mistake {
    return Mistake(documentId, this.mistakeId, this.p, this.r, this.mistakeType, this.description)
}

@Suppress("UNCHECKED_CAST")
fun ByteArrayResource?.toResponse(documentId: String): ResponseEntity<ByteArrayResource?> {
    return ResponseEntity.ok().headers(
        HttpHeaders().also {
            it.contentType = MediaType.APPLICATION_OCTET_STREAM
            it.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$documentId (${LocalDateTime.now()}).docx")
        }
    ).body(this)
}
