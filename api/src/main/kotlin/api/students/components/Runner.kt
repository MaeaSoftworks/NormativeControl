package api.students.components

import api.students.dto.MessageCode
import core.parsers.DocumentParser
import core.rendering.RenderLauncher
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
            val parser = DocumentParser(inputStream)
            parser.runVerification()

            val render = ByteArrayOutputStream().also { RenderLauncher(parser).render(it) }

            val result = ByteArrayOutputStream().also { parser.writeResult(it) }

            callback.write(MessageCode.INFO, "Verified successfully. Uploading results")

            s3.putObject(render.toByteArray(), "$documentId/conclusion.html", mapOf("accessKey" to accessKey))
            s3.putObject(result.toByteArray(), "$documentId/conclusion.docx", mapOf("accessKey" to accessKey))
        } catch (e: Exception) {
            callback.write(MessageCode.ERROR, e.message.toString())
        }

        callback.write(MessageCode.SUCCESS, "Completed")
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
