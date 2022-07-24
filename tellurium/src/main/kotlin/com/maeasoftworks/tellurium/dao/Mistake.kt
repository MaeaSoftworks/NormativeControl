package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.tellurium.documentation.Documentation

@Documentation
class Mistake(
    @get:JsonProperty(value = "mistake-id")
    var mistakeId: Long = 0,

    @get:JsonProperty(value = "paragraph-id")
    val paragraphId: Int?,

    @get:JsonProperty(value = "run-id")
    val runId: Int?,

    @get:JsonProperty(value = "mistake-type")
    val mistakeType: MistakeType,

    @get:JsonProperty(value = "description")
    val mistakeDescription: String? = null
)
