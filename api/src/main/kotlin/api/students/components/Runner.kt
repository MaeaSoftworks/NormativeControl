package api.students.components

import api.students.dto.MessageCode
import api.common.extensions.Functions.retry
import core.parsers.DocumentParser
import core.rendering.RenderLauncher
import java.io.ByteArrayOutputStream

class Runner(
    private var documentId: String,
    private val s3: S3Storage,
    private val callback: ParserCallback
) : Runnable {
    override fun run() {
        val tags = mutableMapOf<String, String>()
        val file = retry<_, Exception>(5, 5, this::class, { it != null }, { s3.getObject("$documentId/source.docx", tags) })
        if (file == null) {
            callback.write(MessageCode.ERROR, "Unable to get file")
            return
        }
        val parser = DocumentParser(file)
        parser.runVerification()

        val render = ByteArrayOutputStream().also {
            RenderLauncher(parser).render(it)
        }

        val result = ByteArrayOutputStream().also {
            parser.writeResult(it)
        }
        callback.write(MessageCode.INFO, "Verified successfully. Uploading results")

        s3.putObject(render.toByteArray(), "$documentId/conclusion.html", mapOf("accessKey" to tags["accessKey"]!!))
        s3.putObject(result.toByteArray(), "$documentId/conclusion.docx", mapOf("accessKey" to tags["accessKey"]!!))

        callback.write(MessageCode.SUCCESS, "Completed")
    }
}
