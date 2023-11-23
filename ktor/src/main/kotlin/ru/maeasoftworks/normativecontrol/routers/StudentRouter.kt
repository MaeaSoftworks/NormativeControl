package ru.maeasoftworks.normativecontrol.routers

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.dto.Message
import ru.maeasoftworks.normativecontrol.dto.UploadMultipart
import ru.maeasoftworks.normativecontrol.extensions.*
import ru.maeasoftworks.normativecontrol.model.startVerification
import ru.maeasoftworks.normativecontrol.modules.S3
import ru.maeasoftworks.normativecontrol.services.StudentService.getFile
import java.util.*

fun Routing.studentRouter() {
    route("/student") {
        route("/document") {

            post("/upload") {
                val multipart = call.receiveMultipart().extractParts<UploadMultipart>()
                val documentId = UUID.randomUUID().toString().filter { it != '-' }
                launch { S3.uploadSource(documentId, multipart.file, multipart.accessKey) }
                val channel = Channel<Message>(Channel.UNLIMITED)
                launch { startVerification(documentId, multipart.accessKey, multipart.file.inputStream(), channel) }
                call.respond(channel.receiveAsFlow())
            }

            get("/conclusion") {
                val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                val filename = conclusion(documentId)
                getFile(accessKey, filename)
            }

            get("/render") {
                val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                val accessKey = call.parameters["accessKey"] ?: throw IllegalArgumentException("accessKey must be not null")
                val filename = render(documentId)
                getFile(accessKey, filename)
            }
        }
    }
}