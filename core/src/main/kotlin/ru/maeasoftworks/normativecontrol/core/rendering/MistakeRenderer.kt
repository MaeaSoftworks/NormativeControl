package ru.maeasoftworks.normativecontrol.core.rendering

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.core.abstractions.MistakeReason
import ru.maeasoftworks.normativecontrol.core.model.ShortMistake

class MistakeRenderer {
    private val mistakes = mutableListOf<ShortMistake>()
    private val foundMistakes = mutableMapOf<MistakeReason, Int>()
    private var last: Int = 0

    fun addMistake(
        mistakeReason: MistakeReason,
        id: String,
        expected: String? = null,
        actual: String? = null
    ) {
        if (mistakeReason !in foundMistakes.keys) {
            foundMistakes[mistakeReason] = last++
        }
        mistakes += ShortMistake(
            foundMistakes[mistakeReason]!!,
            id,
            expected,
            actual
        )
    }

    fun serialize(): String {
        return "let _map = [${foundMistakes.keys.joinToString { "\"${it.description}\"" }}];" +
                "function mistakes() { " +
                "return ${Json.encodeToString(mistakes)}.map(mistake => {" +
                "let description = _map[mistake.c]; " +
                "return { " +
                "mistakeReason: description, " +
                "description: (mistake.a !== null && mistake.e !== null) ? " +
                "description + \": найдено: \" + mistake.a + \", требуется: \" + mistake.e + \".\" : description,id: mistake.i" +
                "};})}"
    }
}