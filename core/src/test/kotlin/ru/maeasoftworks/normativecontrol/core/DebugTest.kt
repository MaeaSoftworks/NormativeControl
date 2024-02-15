package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.configurations.VerificationConfiguration
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import java.io.File

class DebugTest : ShouldSpec({
    beforeEach {
        VerificationConfiguration.initialize {
            forceStyleInlining = false
        }
    }

    should("not throws exceptions") {
        val ctx = VerificationContext(Profile.UrFU)
        Document(ctx).apply {
            load(File("src/test/resources/ignore/sample1.docx").inputStream())
            runVerification()
        }
        println(ctx.render.getString())
    }
})