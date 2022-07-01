package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.docx4nc.enums.Status
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import java.time.LocalDateTime

@Documented("docs.entity.StatusResponse.info")
data class StatusResponse(
    @DocumentedProperty("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @DocumentedProperty("docs.entity.StatusResponse.prop0", Status::class)
    val status: Status,
    @DocumentedProperty("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
