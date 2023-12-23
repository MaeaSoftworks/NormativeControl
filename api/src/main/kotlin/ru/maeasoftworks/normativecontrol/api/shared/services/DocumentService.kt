package ru.maeasoftworks.normativecontrol.api.shared.services

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.api.shared.extensions.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.shared.modules.FileStorage
import ru.maeasoftworks.normativecontrol.api.students.dto.UploadMultipart
import java.nio.ByteBuffer

object DocumentService {
    suspend fun getFileWithAccessKey(accessKey: String, filename: String): Flow<ByteBuffer> {
        if (FileStorage.getTags(filename)?.get("accessKey") == accessKey) {
            return FileStorage.getObject(filename)
        } else {
            throw NoAccessException()
        }
    }

    suspend fun getFileUnsafe(filename: String): Flow<ByteBuffer> = FileStorage.getObject(filename)

    suspend fun uploadSourceDocument(documentId: String, uploadMultipart: UploadMultipart) = coroutineScope {
        launch { FileStorage.uploadSourceDocument(documentId, uploadMultipart.file, uploadMultipart.accessKey) }
    }
}