package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.runBlocking
import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import ru.maeasoftworks.normativecontrol.core.parsers.DocumentParser
import ru.maeasoftworks.normativecontrol.core.rendering.RenderLauncher
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Runner(
    private var documentId: String,
    private var document: Flux<DataBuffer>,
    private var accessKey: String,
    private val s3: S3Storage,
    private val callback: ParserCallback
) : Runnable {
    override fun run() {
        val inputStream = getFile()
        try {
            val parser = DocumentParser()
            parser.load(inputStream)
            runBlocking { parser.runVerification() }

            val render = RenderLauncher(parser).render()

            val result = ByteArrayOutputStream().also { parser.writeResult(it) }

            callback.write(Message.Code.INFO, "Verified successfully. Uploading results")

            s3.putObject(render.toByteArray(), "$documentId/conclusion.html", mapOf("accessKey" to accessKey))
            s3.putObject(result.toByteArray(), "$documentId/conclusion.docx", mapOf("accessKey" to accessKey))
        } catch (e: Exception) {
            callback.write(Message.Code.ERROR, e.message.toString())
        }

        callback.write(Message.Code.SUCCESS, "Completed")
    }

    private fun getFile(): InputStream {
        val out = ByteArrayOutputStream()
        val buffers = document.collectList().block()!!
        for (it in buffers) {
            val bytes = ByteArray(it.readableByteCount())
            it.read(bytes)
            out.write(bytes)
        }
        return ByteArrayInputStream(out.toByteArray())
    }
}
