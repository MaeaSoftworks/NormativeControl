package ru.maeasoftworks.normativecontrol.api.students.controllers

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
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.api.shared.extensions.conclusion
import ru.maeasoftworks.normativecontrol.api.shared.extensions.render
import ru.maeasoftworks.normativecontrol.api.shared.extensions.respond
import ru.maeasoftworks.normativecontrol.api.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.api.shared.utils.extractMultipartParts
import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import ru.maeasoftworks.normativecontrol.api.students.dto.UploadMultipart
import ru.maeasoftworks.normativecontrol.api.students.model.Verifier
import ru.maeasoftworks.normativecontrol.api.shared.services.DocumentService
import java.util.*

class StudentsController(override val di: DI) : Controller() {
    private val documentService: DocumentService by instance()
    private val verifier: Verifier by instance()

    override fun Routing.registerRoutes() {
        route("/student") {
            route("/document") {
                post("/upload") {
                    val channel = Channel<Message>(Channel.UNLIMITED)
                    val documentId = UUID.randomUUID().toString().filter { it != '-' }
                    val multipart = call.extractMultipartParts { UploadMultipart("file".file(), "accessKey".string()) }
                    documentService.uploadSourceDocument(documentId, multipart)
                    launch { verifier.startVerification(documentId, multipart.accessKey, multipart.file.inputStream(), channel) }
                    call.respond(channel.receiveAsFlow())
                }

                get("/conclusion") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = conclusion(documentId)

                    call.respondBytesWriter(
                        ContentType.defaultForFileExtension("docx"),
                        HttpStatusCode.OK,
                        null,
                    ) {
                        documentService.getFileWithAccessKey(accessKey, filename).collect {
                            this.writeFully(it)
                        }
                    }
                }

                get("/render") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = render(documentId)

                    call.respondBytesWriter(
                        ContentType.defaultForFileExtension("html"),
                        HttpStatusCode.OK,
                        null,
                    ) {
                        documentService.getFileWithAccessKey(accessKey, filename).collect {
                            this.writeFully(it)
                        }
                    }
                }
            }
        }
    }
}
