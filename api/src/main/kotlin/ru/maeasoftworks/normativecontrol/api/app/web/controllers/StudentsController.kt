package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.respond
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Event
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.MultipartExtractor.Companion.extractMultipartParts
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import java.io.ByteArrayInputStream

object StudentsController: ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            route("anonymous") {
                route("/verification") {
                    post("/upload") {
                        val file = call.extractMultipartParts { file("file") }
                        val fingerprint = call.request.cookies["fingerprint"]
                            ?: KeyGenerator.generate(64).also {
                                call.response.cookies.append(Cookie("fingerprint", value = it, path = "/student/anonymous"))
                            }

                        val channel = Channel<Message>(Channel.UNLIMITED)
                        val documentId = KeyGenerator.generate(32)
                        val uploading = launch { FileStorage.uploadSourceDocument(documentId, file, fingerprint) }
                        val verification = launch { VerificationService.startVerification(documentId, fingerprint, ByteArrayInputStream(file), channel) }
                        channel.invokeOnClose {
                            if (uploading.isActive) {
                                uploading.cancel()
                            }
                            if (verification.isActive) {
                                verification.cancel()
                            }
                        }
                        call.respond(channel.receiveAsFlow().map { Event.fromMessage(it) })
                    }

                    get("/conclusion") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val fingerprint = call.request.cookies["fingerprint"] ?: throw IllegalArgumentException("fingerprint must be not null")
                        val filename = conclusion(documentId)

                        call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                            if (FileStorage.getTags(filename)?.get("accessKey") != fingerprint) {
                                throw NoAccessException()
                            }
                            FileStorage.getObject(filename).collect {
                                this.writeFully(it)
                            }
                        }
                    }

                    get("/render") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val fingerprint = call.request.cookies["fingerprint"] ?: throw IllegalArgumentException("fingerprint must be not null")
                        val filename = render(documentId)

                        call.respondBytesWriter(ContentType.defaultForFileExtension("html"), HttpStatusCode.OK) {
                            if (FileStorage.getTags(filename)?.get("accessKey") != fingerprint) {
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
}
