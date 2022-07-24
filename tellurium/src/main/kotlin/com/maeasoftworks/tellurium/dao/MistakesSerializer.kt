package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class MistakesSerializer : AttributeConverter<List<Mistake>?, String> {
    override fun convertToDatabaseColumn(mistakes: List<Mistake>?): String {
        return mapper.writeValueAsString(mistakes)
    }

    override fun convertToEntityAttribute(dbData: String): List<Mistake> {
        return mapper.readValue(dbData, object : TypeReference<List<Mistake>>() {})
    }

    companion object {
        val mapper = jacksonObjectMapper()
    }
}