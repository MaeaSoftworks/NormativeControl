package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Component
class MistakesSerializer {
    fun convertToDatabaseColumn(mistakes: List<Mistake>): String {
        return mapper.writeValueAsString(mistakes)
    }

    fun convertToEntityAttribute(dbData: String): List<Mistake> {
        return mapper.readValue(dbData, object : TypeReference<List<Mistake>>() {})
    }

    companion object {
        val mapper = jacksonObjectMapper()
    }
}