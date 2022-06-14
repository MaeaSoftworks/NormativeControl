package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import java.time.LocalDateTime

@Documented("docs.entity.FileResponse.info")
data class FileResponse(
    @DocumentedProperty("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @Suppress("ArrayInDataClass")
    @DocumentedProperty("docs.entity.FileResponse.prop0")
    val file: ByteArray?,
    @DocumentedProperty("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
