package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("Represents document credentials.")
data class QueueResponse(
    @PropertyDocumentation("document id")
    val documentId: String,
    @PropertyDocumentation("document access key which generated on client side")
    val accessKey: String,
    @PropertyDocumentation("request timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)