package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.nio.ByteBuffer

@Component
class DocumentManager(
    private val s3AsyncStorage: S3AsyncStorage,
    private val launcher: Launcher
) {
    fun saveDocumentAndSubscribe(document: FilePart, documentId: String, accessKey: String): Flow<Message> {
        return flow {
            try {
                s3AsyncStorage.putObjectAsync(document, "$documentId/source.docx", mapOf("accessKey" to accessKey))
            } catch (e: Exception) { // todo find causes
                emit(Message(documentId, Message.Code.ERROR, "Error during file uploading"))
            }
            emit(Message(documentId, Message.Code.INFO, "File saved"))
        }.onCompletion {
            if (it == null)
                emitAll(launcher.run(documentId, Flux.from(document.content()), accessKey))
        }
    }

    fun checkAccessAndGetHtml(documentId: String, accessKey: String): Flow<ByteBuffer> {
        return checkAccessAndGetObject("$documentId/conclusion.html", accessKey)
    }

    fun getHtml(documentId: String): Flow<ByteBuffer> {
        return getObject("$documentId/conclusion.html")
    }

    fun checkAccessAndGetConclusion(documentId: String, accessKey: String): Flow<ByteBuffer> {
        return checkAccessAndGetObject("$documentId/conclusion.docx", accessKey)
    }

    fun getConclusion(documentId: String): Flow<ByteBuffer> {
        return getObject("$documentId/conclusion.docx")
    }

    @OptIn(FlowPreview::class)
    private fun checkAccessAndGetObject(objectName: String, accessKey: String): Flow<ByteBuffer> {
        return s3AsyncStorage
            .getTagsAsync(objectName)
            .onEach {
                if (!it.containsKey("accessKey") || it["accessKey"] != accessKey) {
                    throw NoAccessException("You do not have access to this document")
                }
            }.flatMapConcat {
                s3AsyncStorage.getObjectAsync(objectName)
            }
    }

    private fun getObject(objectName: String): Flow<ByteBuffer> {
        return s3AsyncStorage.getObjectAsync(objectName)
    }
}