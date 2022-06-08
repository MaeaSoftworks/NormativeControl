package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dao.Mistake

data class DocumentControlPanelResponse(
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    val password: String,
    val mistakes: List<Mistake>
)