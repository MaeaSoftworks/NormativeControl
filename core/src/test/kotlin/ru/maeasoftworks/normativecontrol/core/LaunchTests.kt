package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.parsers.chapters.BodyParser

class LaunchTests: StringSpec({
    "context text" {
        val ctx = VerificationContext()
        ctx.chapter = BodyParser
        withContext(ctx) {
            delay(500)
            coroutineContext[VerificationContext.Key]?.chapter shouldBe BodyParser
        }
    }
})