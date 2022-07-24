package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.tellurium.dto.documentation.annotations.Documented
import com.maeasoftworks.tellurium.dto.documentation.annotations.DocumentedProperty
import javax.persistence.*
import javax.validation.constraints.NotNull

@Documented("docs.entity.Mistake.info")
class Mistake(
    var mistakeId: Long = 0,

    @DocumentedProperty("docs.entity.Mistake.prop1")
    @get:JsonProperty(value = "paragraph-id")
    val paragraphId: Int?,

    @DocumentedProperty("docs.entity.Mistake.prop2")
    @get:JsonProperty(value = "run-id")
    val runId: Int?,

    @DocumentedProperty("docs.entity.Mistake.prop3", MistakeType::class)
    @get:JsonProperty(value = "mistake-type")
    val mistakeType: MistakeType,

    @DocumentedProperty("docs.entity.Mistake.prop4")
    val description: String? = null
)
