package com.maeasoftworks.bootstrap.model

import com.maeasoftworks.bootstrap.configurations.ValueStorage
import com.maeasoftworks.bootstrap.dto.MessageCode
import com.maeasoftworks.core.parsers.DocumentParser
import com.maeasoftworks.rendering.RenderLauncher
import io.minio.GetObjectArgs
import io.minio.MinioClient
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger

class Runner(
    private var documentId: String,
    private val count: AtomicInteger,
    private val minioClient: MinioClient,
    private val parserCallback: ParserCallback
) : Runnable {
    override fun run() {
        val file = getFile()
        val parser = DocumentParser(file)
        try {
            parser.runVerification()
        } catch (e: Exception) {
            parserCallback.write(MessageCode.ERROR, "Parsing error")
        }
        val stream = ByteArrayOutputStream()
        try {
            RenderLauncher(parser).render(stream)
        } catch (e: Exception) {
            parserCallback.write(MessageCode.ERROR, "Rendering error")
        }
        parser.addCommentsAndSave()
        count.decrementAndGet()
        parserCallback.write(MessageCode.SUCCESS, "Completed")
    }

    private fun getFile(): ByteArrayInputStream {
        (minioClient.getObject(GetObjectArgs.builder().bucket(ValueStorage.bucket).`object`("$documentId.docx").build()) as InputStream)
            .use { inputStream ->
                val buff = ByteArray(1000)
                var bytesRead: Int
                val bao = ByteArrayOutputStream()
                while (inputStream.read(buff).also { bytesRead = it } != -1) {
                    bao.write(buff, 0, bytesRead)
                }
                return ByteArrayInputStream(bao.toByteArray())
            }
    }
}
