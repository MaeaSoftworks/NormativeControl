package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import me.prmncr.hotloader.HotLoader
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.File

class DebugTest : ShouldSpec({
    beforeTest {
        HotLoader.load()
    }

    should("not throws exceptions") {
        Document(VerificationContext(Profile.UrFU)).apply {
            load(File("src/test/resources/ignore/different sized parts.docx").inputStream())
            runVerification()
        }
    }
})