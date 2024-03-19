package normativecontrol.core.abstractions.mistakes

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MistakeSerializer {
    private val mistakes = mutableListOf<Mistake>()
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
        mistakes += Mistake(
            foundMistakes[mistakeReason]!!,
            id,
            expected,
            actual
        )
    }

    fun serialize(): String {
        @Suppress("JSUnusedLocalSymbols")
        return """
            let _map = [${foundMistakes.keys.joinToString { "\"${it.description}\"" }}];
            function mistakes() { 
                return ${Json.encodeToString(mistakes)}.map(mistake => {
                    let description = _map[mistake.c];
                    return {
                        description: (mistake.a && mistake.e) ? description + ": найдено: " + mistake.a + ", требуется: " + mistake.e + "." : description,
                        id: mistake.i
                    }
                })
            }
            """.trimIndent()
            .replace(Regex("(\n*)\n"), "$1")
            .replace("    ", "")
    }
}