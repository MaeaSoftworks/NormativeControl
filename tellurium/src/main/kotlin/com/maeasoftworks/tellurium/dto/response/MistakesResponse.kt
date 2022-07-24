package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.dao.Mistake
import com.maeasoftworks.tellurium.documentation.Documentation
import java.time.LocalDateTime

@Documentation
data class MistakesResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    val mistakes: List<Mistake>,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
