package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("Represents file.")
data class FileResponse(
    @PropertyDocumentation("document id")
    val documentId: String,
    @Suppress("ArrayInDataClass")
    @PropertyDocumentation("file serialized as Base64 string")
    val file: ByteArray?,
    @PropertyDocumentation("request timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
