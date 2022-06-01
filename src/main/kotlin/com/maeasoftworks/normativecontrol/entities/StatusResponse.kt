package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import com.maeasoftworks.normativecontrol.parser.enums.Status
import java.time.LocalDateTime

@Documentation("Represents state of document.")
data class StatusResponse(
    @PropertyDocumentation("document id")
    val documentId: String,
    @PropertyDocumentation("status of document. Can be:", Status::class)
    val status: Status,
    @PropertyDocumentation("request timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)