package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.withContext
import me.prmncr.hotloader.HotLoader
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.File

class DebugTest : ShouldSpec({
    beforeTest {
        HotLoader.load()
    }

    should("not throws exceptions") {
        val ctx = VerificationContext(Profile.UrFU)
        withContext(ctx) {
            Document(ctx).apply {
                load(File("src/test/resources/ignore/different sized parts.docx").inputStream())
                runVerification()
            }
        }
    }
})