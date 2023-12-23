package ru.maeasoftworks.normativecontrol.api.shared.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.api.shared.extensions.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.shared.modules.S3
import ru.maeasoftworks.normativecontrol.api.students.dto.UploadMultipart
import java.nio.ByteBuffer


object DocumentService {
    suspend fun getFileWithAccessKey(accessKey: String, filename: String): Flow<ByteBuffer> {
        if (S3.getTags(filename)["accessKey"] == accessKey) {
            return S3.getObject(filename)
        } else {
            throw NoAccessException()
        }
    }

    suspend fun getFileUnsafe(filename: String): Flow<ByteBuffer> = S3.getObject(filename)

    suspend fun uploadSourceDocument(documentId: String, uploadMultipart: UploadMultipart) = coroutineScope {
        launch { S3.uploadSourceDocument(documentId, uploadMultipart.file, uploadMultipart.accessKey) }
    }
}