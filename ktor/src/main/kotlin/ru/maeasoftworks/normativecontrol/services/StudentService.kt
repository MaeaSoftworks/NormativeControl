package ru.maeasoftworks.normativecontrol.services

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.maeasoftworks.normativecontrol.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.modules.S3
import ru.maeasoftworks.normativecontrol.utils.Pipe

object StudentService {
    suspend fun Pipe.getFile(accessKey: String, filename: String) {
        if (S3.getTags(filename)["accessKey"] == accessKey) {
            call.respondBytesWriter(
                ContentType.defaultForFileExtension("docx"),
                HttpStatusCode.OK,
                null,
            ) {
                val file = S3.getObject(filename)
                file.collect {
                    this.writeFully(it)
                }
            }
        } else {
            throw NoAccessException()
        }
    }
}