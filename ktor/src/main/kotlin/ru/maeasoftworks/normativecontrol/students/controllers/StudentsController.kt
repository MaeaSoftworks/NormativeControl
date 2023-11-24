package ru.maeasoftworks.normativecontrol.students.controllers

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.shared.extensions.conclusion
import ru.maeasoftworks.normativecontrol.shared.extensions.render
import ru.maeasoftworks.normativecontrol.shared.extensions.respond
import ru.maeasoftworks.normativecontrol.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.shared.utils.extractMultipartParts
import ru.maeasoftworks.normativecontrol.students.dto.Message
import ru.maeasoftworks.normativecontrol.students.dto.UploadMultipart
import ru.maeasoftworks.normativecontrol.students.model.Verifier
import ru.maeasoftworks.normativecontrol.students.services.StudentDocumentService
import java.util.*

class StudentsController(override val di: DI) : Controller() {
    private val studentDocumentService: StudentDocumentService by instance()
    private val verifier: Verifier by instance()

    override fun Routing.registerRoutes() {
        route("/student") {
            route("/document") {
                post("/upload") {
                    val channel = Channel<Message>(Channel.UNLIMITED)
                    val documentId = UUID.randomUUID().toString().filter { it != '-' }
                    val multipart = call.extractMultipartParts { UploadMultipart("file".byteArray, "accessKey".string) }
                    studentDocumentService.uploadSourceDocument(documentId, multipart)
                    launch { verifier.startVerification(documentId, multipart.accessKey, multipart.file.inputStream(), channel) }
                    call.respond(channel.receiveAsFlow())
                }

                get("/conclusion") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = conclusion(documentId)
                    studentDocumentService.getFile(call, accessKey, filename)
                }

                get("/render") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                    val filename = render(documentId)
                    studentDocumentService.getFile(call, accessKey, filename)
                }
            }
        }
    }
}