package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.dao.IMistake
import java.time.LocalDateTime

data class MistakesResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    val mistakes: List<IMistake>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
