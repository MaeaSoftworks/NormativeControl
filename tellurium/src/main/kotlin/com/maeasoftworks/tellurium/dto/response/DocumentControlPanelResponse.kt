package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.dao.Mistake
import com.maeasoftworks.tellurium.documentation.Documentation
import java.time.LocalDateTime

@Documentation
data class DocumentControlPanelResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    val password: String,
    val mistakes: List<Mistake>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
