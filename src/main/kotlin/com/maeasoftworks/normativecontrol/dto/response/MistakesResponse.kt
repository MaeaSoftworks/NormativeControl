package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dao.Mistake
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import java.time.LocalDateTime

@Documented("docs.entity.MistakesResponse.info")
data class MistakesResponse(
    @DocumentedProperty("docs.entity.common.id")
    @get:JsonProperty(value = "document-id")
    val documentId: String,
    @DocumentedProperty("docs.entity.MistakesResponse.prop0")
    val errors: List<Mistake>,
    @DocumentedProperty("docs.entity.common.time")
    val timestamp: LocalDateTime = LocalDateTime.now()
)
