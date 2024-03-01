package normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import java.io.File

class SchemaTests: ShouldSpec({
    should("serialize chapters properly") {
        val objects = Json.decodeFromString<Array<Chapter>>(File("src/test/resources/chapters.json").readText())
        objects shouldHaveSize 10
    }

    should("create configuration object from schema") {
        val objects = Json.decodeFromString<Array<Chapter>>(File("src/test/resources/chapters.json").readText())
        val config = ChapterConfiguration(objects)
        config.names.size shouldBe 10
    }

    should("detect first chapter properly") {
        val objects = Json.decodeFromString<Array<Chapter>>(File("src/test/resources/chapters.json").readText())
        ChapterConfiguration(objects).startChapter.code shouldBe "1"
    }
})