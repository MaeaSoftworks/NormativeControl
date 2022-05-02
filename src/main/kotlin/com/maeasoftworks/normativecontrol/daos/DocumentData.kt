package com.maeasoftworks.normativecontrol.daos

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.maeasoftworks.normativecontrol.dtos.Error
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table

@Table
@Entity
class DocumentData {
    @Id
    private lateinit var id: String

    @Lob
    private lateinit var errors: String

    constructor()

    constructor(id: String, errors: List<Error>?) {
        this.id = id
        var jsonErrors = ""
        try {
            jsonErrors = ObjectMapper().writeValueAsString(errors)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        this.errors = jsonErrors
    }

    fun getDeserializedErrors(): List<Error> =
        ObjectMapper().readValue(errors, object : TypeReference<List<Error>>() {})
}