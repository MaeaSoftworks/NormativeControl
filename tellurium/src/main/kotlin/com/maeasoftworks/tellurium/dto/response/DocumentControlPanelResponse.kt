package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.dao.IMistake
import java.time.LocalDateTime

data class DocumentControlPanelResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    val password: String,
    val mistakes: List<IMistake>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
