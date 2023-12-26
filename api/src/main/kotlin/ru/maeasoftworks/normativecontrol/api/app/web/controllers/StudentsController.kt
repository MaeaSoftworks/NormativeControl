package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.respond
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.extractMultipartParts
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.UploadMultipart
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import java.util.*

object StudentsController: ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            route("/document") {
                post("/upload") {
                    val channel = Channel<Message>(Channel.UNLIMITED)
                    val documentId = UUID.randomUUID().toString().filter { it != '-' }
                    val multipart = call.extractMultipartParts { UploadMultipart("file".file(), "accessKey".string()) }
                    launch { FileStorage.uploadSourceDocument(documentId, multipart.file, multipart.accessKey) }
                    launch { VerificationService.startVerification(documentId, multipart.accessKey, multipart.file.inputStream(), channel) }
                    call.respond(channel.receiveAsFlow())
                }

                get("/conclusion") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = conclusion(documentId)

                    call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                        if (FileStorage.getTags(filename)?.get("accessKey") != accessKey) {
                            throw NoAccessException()
                        }
                        FileStorage.getObject(filename).collect {
                            this.writeFully(it)
                        }
                    }
                }

                get("/render") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = render(documentId)

                    call.respondBytesWriter(ContentType.defaultForFileExtension("html"), HttpStatusCode.OK) {
                        if (FileStorage.getTags(filename)?.get("accessKey") != accessKey) {
                            throw NoAccessException()
                        }
                        FileStorage.getObject(filename).collect {
                            this.writeFully(it)
                        }
                    }
                }
            }
        }
    }
}
