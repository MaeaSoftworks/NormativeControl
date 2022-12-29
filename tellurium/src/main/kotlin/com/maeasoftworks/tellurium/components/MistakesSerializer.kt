package com.maeasoftworks.tellurium.components

import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.tellurium.dto.response.Mistake
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Serializer for mistakes list in O(n).
 *
 * Format:
 * `~$mistakeId;$paragraphId;$runId;$mistakeType;$description`
 * todo: remove this class: change format or add mistakes table
 */
@Converter
class MistakesSerializer : AttributeConverter<List<Mistake>, String> {
    override fun convertToDatabaseColumn(attribute: List<Mistake>): String {
        val result = StringBuilder()
        for (m in attribute) {
            result.append("~${m.mistakeId}")
            result.append(';')
            if (m.paragraphId != null) {
                result.append(m.paragraphId)
            }
            result.append(';')
            if (m.runId != null) {
                result.append(m.runId)
            }
            result.append(";${m.mistakeType.name};")
            if (m.mistakeDescription != null) {
                result.append(m.mistakeDescription)
            }
        }
        return result.toString()
    }

    override fun convertToEntityAttribute(dbData: String): List<Mistake> {
        val currentSequence = StringBuilder()
        var currentMistake = Mistake()
        var currentProperty = 0
        val mistakes = mutableListOf<Mistake>()
        for (pos in dbData.indices) {
            when (dbData[pos]) {
                '~' -> {
                    if (pos != 0) {
                        when (currentProperty) {
                            0 -> currentMistake.mistakeId = currentSequence.toString().toLong()
                            1 -> currentMistake.paragraphId = currentSequence.toString().toInt()
                            2 -> currentMistake.runId = currentSequence.toString().toInt()
                            3 -> currentMistake.mistakeType = MistakeType.valueOf(currentSequence.toString())
                            4 -> currentMistake.mistakeDescription = currentSequence.toString()
                        }
                        currentSequence.clear()
                        mistakes += currentMistake
                        currentMistake = Mistake()
                        currentProperty = 0
                    }
                }

                ';' -> {
                    when (currentProperty) {
                        0 -> currentMistake.mistakeId = currentSequence.toString().toLong()
                        1 -> if (currentSequence.isNotBlank()) currentMistake.paragraphId =
                            currentSequence.toString().toInt()

                        2 -> if (currentSequence.isNotBlank()) currentMistake.runId = currentSequence.toString().toInt()
                        3 -> currentMistake.mistakeType = MistakeType.valueOf(currentSequence.toString())
                        4 -> currentMistake.mistakeDescription = currentSequence.toString()
                    }
                    currentSequence.clear()
                    currentProperty++
                }

                else -> currentSequence.append(dbData[pos])
            }
        }
        return mistakes
    }
}
