package com.maeasoftworks.normativecontrol.entities

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import java.time.LocalDateTime

@Documentation("docs.entity.MistakesResponse.info")
data class MistakesResponse(
    @PropertyDocumentation("docs.entity.common.id")
    val documentId: String,
    @PropertyDocumentation("docs.entity.MistakesResponse.prop0")
    val errors: List<Mistake>,
    @PropertyDocumentation("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)