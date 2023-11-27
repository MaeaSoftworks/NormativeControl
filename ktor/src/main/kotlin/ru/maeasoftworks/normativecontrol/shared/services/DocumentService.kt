package ru.maeasoftworks.normativecontrol.shared.services

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondBytesWriter
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.shared.extensions.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.shared.modules.S3
import ru.maeasoftworks.normativecontrol.shared.utils.Service
import ru.maeasoftworks.normativecontrol.students.dto.UploadMultipart
import java.nio.ByteBuffer

class DocumentService(override val di: DI) : Service() {
    private val s3: S3 by instance()

    suspend fun getFileWithAccessKey(accessKey: String, filename: String): Flow<ByteBuffer> {
        if (s3.getTags(filename)["accessKey"] == accessKey) {
            return s3.getObject(filename)
        } else {
            throw NoAccessException()
        }
    }

    suspend fun getFileUnsafe(filename: String): Flow<ByteBuffer>  = s3.getObject(filename)

    suspend fun uploadSourceDocument(documentId: String, uploadMultipart: UploadMultipart) = coroutineScope {
        launch { s3.uploadSourceDocument(documentId, uploadMultipart.file, uploadMultipart.accessKey) }
    }
}