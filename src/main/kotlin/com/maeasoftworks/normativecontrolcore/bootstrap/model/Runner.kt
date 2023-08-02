package com.maeasoftworks.normativecontrolcore.bootstrap.model

import com.maeasoftworks.normativecontrolcore.bootstrap.extensions.Functions.retry
import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import com.maeasoftworks.normativecontrolcore.rendering.RenderLauncher
import io.minio.GetObjectArgs
import io.minio.GetObjectTagsArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger

class Runner(
    private var documentId: String,
    private val count: AtomicInteger,
    private val minioClient: MinioClient,
    private val callback: ParserCallback
) : Runnable {
    override fun run() {
        try {
            val tags = mutableMapOf<String, String>()
            val file = retry (5, 5, this::class, { it != null }, { getFile(tags) })
            val parser = DocumentParser(file)
            parser.runVerification()

            val render = ByteArrayOutputStream().also {
                RenderLauncher(parser).render(it)
            }

            val result = ByteArrayOutputStream().also {
                parser.writeResult(it)
            }
            callback.write(com.maeasoftworks.normativecontrolcore.bootstrap.dto.MessageCode.INFO, "Verified successfully. Uploading results")

            uploadObject(ByteArrayInputStream(render.toByteArray()), "html", mapOf("accessKey" to tags["accessKey"]!!))
            uploadObject(ByteArrayInputStream(result.toByteArray()), "result.docx", mapOf("accessKey" to tags["accessKey"]!!))

        } catch (e: Exception) {
            callback.write(com.maeasoftworks.normativecontrolcore.bootstrap.dto.MessageCode.ERROR, "An error occurred during Runner's work. Cause: ${e.message}; ${e.cause}")
        }
        count.decrementAndGet()
        callback.write(com.maeasoftworks.normativecontrolcore.bootstrap.dto.MessageCode.SUCCESS, "Completed")
    }

    private fun getFile(tags: MutableMap<String, String>): ByteArrayInputStream {
        val bao = ByteArrayOutputStream()
        (minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(com.maeasoftworks.normativecontrolcore.bootstrap.configurations.ValueStorage.bucket)
                .`object`("$documentId.docx")
                .build()
        ) as InputStream).use { inputStream ->
            val buffer = ByteArray(1000)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                bao.write(buffer, 0, bytesRead)
            }
        }
        val tags1 = minioClient.getObjectTags(
            GetObjectTagsArgs
                .builder()
                .bucket(com.maeasoftworks.normativecontrolcore.bootstrap.configurations.ValueStorage.bucket)
                .`object`("$documentId.docx")
                .build()
        )
        tags.putAll(tags1.get())
        return ByteArrayInputStream(bao.toByteArray())
    }

    private fun uploadObject(file: ByteArrayInputStream, extension: String, tags: Map<String, String> = mapOf()) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(com.maeasoftworks.normativecontrolcore.bootstrap.configurations.ValueStorage.bucket)
                .`object`("$documentId.$extension")
                .stream(file, file.available().toLong(), -1)
                .tags(tags)
                .build()
        )
    }
}
