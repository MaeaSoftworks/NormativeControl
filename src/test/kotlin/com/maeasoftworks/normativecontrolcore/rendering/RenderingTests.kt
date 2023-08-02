package com.maeasoftworks.normativecontrolcore.rendering

import com.maeasoftworks.normativecontrolcore.core.ParserTestFactory
import com.maeasoftworks.normativecontrolcore.rendering.RenderLauncher
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class RenderingTests : ParserTestFactory(RenderingTests::class) {
    @Test
    fun `sample test`() {
        val parser = createParser("full test.docx")
        parser.runVerification()
        val stream = ByteArrayOutputStream()
        RenderLauncher(parser).render(stream)
        FileOutputStream("src/test/resources/rendering/3.html").use { outputStream -> stream.writeTo(outputStream) }
        val stream2 = ByteArrayOutputStream()
        parser.writeResult(stream2)
        FileOutputStream("src/test/resources/rendering/3.docx").use { outputStream -> stream2.writeTo(outputStream) }
    }
}