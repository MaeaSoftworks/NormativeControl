package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.tellurium.dao.IMistake

class Mistake(
    @get:JsonProperty(value = "mistake-id")
    override var mistakeId: Long = 0,

    @get:JsonProperty(value = "paragraph-id")
    override var paragraphId: Int?,

    @get:JsonProperty(value = "run-id")
    override var runId: Int?,

    @get:JsonProperty(value = "mistake-type")
    override var mistakeType: MistakeType,

    @get:JsonProperty(value = "description")
    override var mistakeDescription: String? = null
) : IMistake {
    constructor() : this(0, null, null, MistakeType.TODO_ERROR, null)
}