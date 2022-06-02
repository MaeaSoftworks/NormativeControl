package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("docs.entity.FileResponse.info")
data class FileResponse(
    @PropertyDocumentation("docs.entity.common.id")
    val documentId: String,
    @Suppress("ArrayInDataClass")
    @PropertyDocumentation("docs.entity.FileResponse.prop0")
    val file: ByteArray?,
    @PropertyDocumentation("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
