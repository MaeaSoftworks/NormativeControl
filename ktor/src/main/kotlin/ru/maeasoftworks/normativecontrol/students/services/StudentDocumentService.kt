package ru.maeasoftworks.normativecontrol.students.services

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondBytesWriter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.shared.extensions.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.shared.modules.S3
import ru.maeasoftworks.normativecontrol.shared.utils.Service
import ru.maeasoftworks.normativecontrol.students.dto.UploadMultipart

class StudentDocumentService(override val di: DI) : Service() {
    private val s3: S3 by instance()

    suspend fun getFile(call: ApplicationCall, accessKey: String, filename: String) {
        if (s3.getTags(filename)["accessKey"] == accessKey) {
            call.respondBytesWriter(
                ContentType.defaultForFileExtension("docx"),
                HttpStatusCode.OK,
                null,
            ) {
                val file = s3.getObject(filename)
                file.collect {
                    this.writeFully(it)
                }
            }
        } else {
            throw NoAccessException()
        }
    }

    suspend fun uploadSourceDocument(documentId: String, uploadMultipart: UploadMultipart) = coroutineScope {
        launch { s3.uploadSourceDocument(documentId, uploadMultipart.file, uploadMultipart.accessKey) }
    }
}