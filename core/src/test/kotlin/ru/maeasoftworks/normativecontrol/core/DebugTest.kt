package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.withContext
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.File

class DebugTest : ShouldSpec({
    should("not throws exceptions") {
        val ctx = VerificationContext(Profile.UrFU)
        withContext(ctx) {
            Document(ctx).apply {
                load(File("src/test/resources/ignore/sample1.docx").inputStream())
                runVerification()
            }
        }
        println(ctx.render.getString())
    }
})