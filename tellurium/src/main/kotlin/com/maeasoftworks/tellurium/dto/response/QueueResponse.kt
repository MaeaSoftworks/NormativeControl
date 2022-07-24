package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.documentation.Documentation
import java.time.LocalDateTime

@Documentation
data class QueueResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
