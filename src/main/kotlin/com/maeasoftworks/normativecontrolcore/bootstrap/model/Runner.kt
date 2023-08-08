package com.maeasoftworks.normativecontrolcore.bootstrap.model

import com.maeasoftworks.normativecontrolcore.bootstrap.adapters.S3Adapter
import com.maeasoftworks.normativecontrolcore.bootstrap.dto.MessageCode
import com.maeasoftworks.normativecontrolcore.bootstrap.extensions.Functions.retry
import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import com.maeasoftworks.normativecontrolcore.rendering.RenderLauncher
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicInteger

class Runner(
    private var documentId: String,
    private val count: AtomicInteger,
    private val s3: S3Adapter,
    private val callback: ParserCallback
) : Runnable {
    override fun run() {
        val tags = mutableMapOf<String, String>()
        val file = retry<_, Exception>(5, 5, this::class, { it != null }, { s3.getObject("$documentId.docx", tags) })
        if (file == null) {
            callback.write(MessageCode.ERROR, "Unable to get file")
            count.decrementAndGet()
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

        s3.putObject(render.toByteArray(), "$documentId.html", mapOf("accessKey" to tags["accessKey"]!!))
        s3.putObject(result.toByteArray(), "$documentId.result.docx", mapOf("accessKey" to tags["accessKey"]!!))

        count.decrementAndGet()
        callback.write(MessageCode.SUCCESS, "Completed")
    }
}
