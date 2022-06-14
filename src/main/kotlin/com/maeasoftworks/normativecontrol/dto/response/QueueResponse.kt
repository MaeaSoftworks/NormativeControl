package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import java.time.LocalDateTime

@Documented("docs.entity.QueueResponse.info")
data class QueueResponse(
    @DocumentedProperty("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @DocumentedProperty("docs.entity.common.key")
    @get:JsonProperty(value = "access-key")
    val accessKey: String,
    @DocumentedProperty("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
