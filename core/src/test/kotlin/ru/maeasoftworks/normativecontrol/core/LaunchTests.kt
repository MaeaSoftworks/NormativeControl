package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.prmncr.hotloader.HotLoader
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.BodyChapter
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

class LaunchTests : StringSpec({

    beforeTest {
        HotLoader.load()
    }

    "context text".config(enabled = false) {
        val ctx = VerificationContext(Profile.UrFU)
        ctx.chapter = BodyChapter
        withContext(ctx) {
            delay(500)
            coroutineContext[VerificationContext.Key]?.chapter shouldBe BodyChapter
        }
    }
})