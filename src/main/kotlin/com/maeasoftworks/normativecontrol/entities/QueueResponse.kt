package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("docs.entity.QueueResponse.info")
data class QueueResponse(
    @PropertyDocumentation("docs.entity.common.id")
    val documentId: String,
    @PropertyDocumentation("docs.entity.common.key")
    val accessKey: String,
    @PropertyDocumentation("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)