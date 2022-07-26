package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.Status
import java.time.LocalDateTime

data class StatusResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    val status: Status,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
