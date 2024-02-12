package ru.maeasoftworks.normativecontrol.core.rendering

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.core.abstractions.MistakeReason
import ru.maeasoftworks.normativecontrol.core.model.DetailedMistake
import ru.maeasoftworks.normativecontrol.core.model.ShortMistake

class MistakeRenderer {
    private val mistakes = mutableListOf<ShortMistake>()
    private val foundMistakes = mutableMapOf<MistakeReason, Int>()
    private var last: Int = 0

    fun addMistake(mistake: DetailedMistake) {
        if (mistake.mistakeReason !in foundMistakes.keys) {
            foundMistakes[mistake.mistakeReason] = last++
        }
        mistakes += ShortMistake(
            foundMistakes[mistake.mistakeReason]!!,
            mistake.id,
            mistake.expected,
            mistake.actual
        )
    }

    fun serialize(): String {
        return "let _map = [${foundMistakes.keys.joinToString { "\"${it.description}\"" }}];" +
                "function mistakes() { " +
                "return ${ Json.encodeToString(mistakes) }.map(mistake => {" +
                "let description = _map[mistake.code]; " +
                "return { " +
                "mistakeReason: description, " +
                "description: (mistake.actual !== null && mistake.expected !== null) ? " +
                "description + \": найдено: \" + mistake.actual + \", требуется: \" + mistake.expected + \".\" : description,id: mistake.id" +
                "};})}"
    }
}