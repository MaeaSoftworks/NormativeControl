package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.MistakeType

interface IMistake {
    @get:JsonProperty(value = "mistake-id")
    var mistakeId: Long
    @get:JsonProperty(value = "paragraph-id")
    var paragraphId: Int?
    @get:JsonProperty(value = "run-id")
    var runId: Int?
    @get:JsonProperty(value = "mistake-type")
    var mistakeType: MistakeType
    @get:JsonProperty(value = "description")
    var mistakeDescription: String?
}