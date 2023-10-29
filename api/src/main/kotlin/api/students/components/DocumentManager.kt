package api.students.components

import api.students.dto.Message
import api.students.dto.MessageCode
import api.common.exceptions.NoAccessException
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.nio.ByteBuffer

@Component
class DocumentManager(
    private val s3AsyncStorage: S3AsyncStorage,
    private val launcher: Launcher
) {
    fun saveDocumentAndSubscribe(document: FilePart, documentId: String, accessKey: String): Flux<Message> {
        return Flux.concat(
            s3AsyncStorage
                .putObjectAsync(
                    document,
                    "$documentId/source.docx",
                    mapOf("accessKey" to accessKey)
                )
                .handle { it, sink ->
                    if (it) {
                        sink.next(Message(documentId, MessageCode.INFO, "File saved"))
                    } else {
                        sink.next(Message(documentId, MessageCode.ERROR, "Error during file uploading"))
                    }
                },

            launcher.run(documentId, Flux.from(document.content()), accessKey)
        )
    }

    fun checkAccessAndGetHtml(documentId: String, accessKey: String): Flux<ByteBuffer> {
        return checkAccessAndGetObject("$documentId/conclusion.html", accessKey)
    }

    fun getHtml(documentId: String): Flux<ByteBuffer> {
        return getObject("$documentId/conclusion.html")
    }

    fun checkAccessAndGetConclusion(documentId: String, accessKey: String): Flux<ByteBuffer> {
        return checkAccessAndGetObject("$documentId/conclusion.docx", accessKey)
    }

    fun getConclusion(documentId: String): Flux<ByteBuffer> {
        return getObject("$documentId/conclusion.docx")
    }

    private fun checkAccessAndGetObject(objectName: String, accessKey: String): Flux<ByteBuffer> {
        return s3AsyncStorage
            .getTagsAsync(objectName)
            .handle { it, sink ->
                if (!it.containsKey("accessKey") || it["accessKey"] != accessKey) {
                    sink.error(NoAccessException("You do not have access to this document"))
                }
                else sink.next(it)
            }
            .thenMany(s3AsyncStorage.getObjectAsync(objectName))
    }

    private fun getObject(objectName: String): Flux<ByteBuffer> {
        return s3AsyncStorage.getObjectAsync(objectName)
    }
}