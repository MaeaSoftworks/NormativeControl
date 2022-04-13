package com.prmncr.normativecontrol.dbos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.serializers.ByteArraySerializer
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table

@Entity
@Table
class ProcessedDocument {
    @Id
    @JsonIgnore
    var id: String? = null
        private set

    @JsonSerialize(using = ByteArraySerializer::class)
    @Lob
    lateinit var file: ByteArray
        private set
    private var errors: String? = null

    constructor(id: String?, file: ByteArray, errors: List<Error?>?) {
        this.id = id
        this.file = file
        this.errors = ObjectMapper().writeValueAsString(errors)
    }

    constructor(id: String?, file: ByteArray, errors: String?) {
        this.id = id
        this.file = file
        this.errors = errors
    }

    constructor() {}

    private inline fun <reified T> ObjectMapper.readValue(s: String): T = this.readValue(s, object : TypeReference<T>() {})

    fun getDeserializedErrors(): List<Error> {
        return ObjectMapper().readValue<List<Error>>(errors!!)
    }
}