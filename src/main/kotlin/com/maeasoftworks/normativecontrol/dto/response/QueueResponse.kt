package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("docs.entity.QueueResponse.info")
data class QueueResponse(
    @PropertyDocumentation("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @PropertyDocumentation("docs.entity.common.key")
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    @PropertyDocumentation("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)