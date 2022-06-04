package com.maeasoftworks.normativecontrol.entities

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import com.maeasoftworks.normativecontrol.parser.enums.Status
import java.time.LocalDateTime

@Documentation("docs.entity.StatusResponse.info")
data class StatusResponse(
    @PropertyDocumentation("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @PropertyDocumentation("docs.entity.StatusResponse.prop0", Status::class)
    val status: Status,
    @PropertyDocumentation("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)