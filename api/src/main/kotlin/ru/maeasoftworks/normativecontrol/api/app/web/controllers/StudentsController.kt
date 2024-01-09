package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.host
import io.ktor.server.request.path
import io.ktor.server.request.port
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Event
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.VerificationInitialization
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Security
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.withRoles
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Box
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Boxed
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.MultipartExtractor.Companion.extractMultipartParts
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.WebSockets
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.respond
import java.io.ByteArrayInputStream
import kotlin.math.ceil

object StudentsController : ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            authenticate(Security.JWT.CONFIGURATION_NAME) {
                withRoles(Role.STUDENT) {
                    route("/secured") {
                        route("/document") {
                            webSocket("/verify") {
                                val len = Boxed<Int>()
                                var file: ByteArray? = null
                                val pos = Boxed(0)
                                incoming.receiveAsFlow().collect { frame ->
                                    if (!len.isInitialized) {
                                        getMetadata(frame, null, len)
                                        file = ByteArray(len.value)
                                        send("Upload was initialized. Waiting for ${ceil(len.value * 1.0 / WebSockets.maxFrameSize).toInt()} file frames...")
                                        return@collect
                                    }
                                    if (pos.value < len.value) {
                                        val added = fillFile(frame, file!!, pos)
                                        send(Frame.Text("Added $added bytes; received ${pos.value} of ${len.value} bytes."))
                                    }
                                    if (pos.value == len.value) {
                                        send("All data received.")
                                        val channel = Channel<Message>(Channel.UNLIMITED)
                                        val documentId = KeyGenerator.generate(32)
                                        verifyFile(documentId, channel, null, file!!)
                                        transaction {
                                            DocumentRepository.save(
                                                Document(
                                                    documentId,
                                                    call.authentication.principal<JWTPrincipal>()!!.subject!!
                                                )
                                            )
                                        }
                                        launch {
                                            channel.receiveAsFlow()
                                                .map { message -> Frame.Text(message.toString()) }
                                                .collect { i -> send(i) }
                                            close()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            route("/anonymous") {
                route("/document") {
                    webSocket("/verify") {
                        val fingerprint = Boxed.Nullable<String>()
                        val len = Boxed<Int>()
                        var file: ByteArray? = null
                        val pos = Boxed(0)
                        incoming.receiveAsFlow().collect { frame ->
                            if (!len.isInitialized && !fingerprint.isInitialized) {
                                getMetadata(frame, fingerprint, len)
                                file = ByteArray(len.value)
                                send("Upload was initialized. Waiting for ${ceil(len.value * 1.0 / WebSockets.maxFrameSize).toInt()} file frames...")
                                return@collect
                            }
                            if (pos.value < len.value) {
                                val added = fillFile(frame, file!!, pos)
                                send(Frame.Text("Added $added bytes; received ${pos.value} of $len bytes."))
                            }
                            if (pos.value == len.value) {
                                send("All data received.")
                                val channel = Channel<Message>(Channel.UNLIMITED)
                                verifyFile(KeyGenerator.generate(32), channel, fingerprint.value!!, file!!)
                                launch {
                                    channel.receiveAsFlow()
                                        .map { message -> Frame.Text(message.toString()) }
                                        .collect { i -> send(i) }
                                    close()
                                }
                            }
                        }
                    }

                    post("/verify") {
                        val file = call.extractMultipartParts { file("file") }
                        val fingerprint = call.request.cookies["fingerprint"]
                            ?: KeyGenerator.generate(64).also {
                                call.response.cookies.append(Cookie("fingerprint", value = it, path = "/student/anonymous"))
                            }
                        val channel = Channel<Message>(Channel.UNLIMITED)
                        verifyFile(KeyGenerator.generate(32), channel, fingerprint, file)
                        val warn = Message.Warn(
                            "SSE `${call.request.host()}:${call.request.port()}/${call.request.path()}` is deprecated. " +
                                    "Please, use WebSocket: `ws://${call.request.host()}:${call.request.port()}/${call.request.path()}` instead."
                        )
                        call.respond(
                            channel.receiveAsFlow().onCompletion { emit(warn) }.map { Event.fromMessage(it) },
                            HttpStatusCode(299, "Deprecated")
                        )
                    }

                    get("/conclusion") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val fingerprint = call.request.cookies["fingerprint"] ?: throw IllegalArgumentException("fingerprint must be not null")
                        val filename = conclusion(documentId)

                        call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                            if (FileStorage.getTags(filename)?.get("fingerprint") != fingerprint) {
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
                            if (FileStorage.getTags(filename)?.get("fingerprint") != fingerprint) {
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

    private fun fillFile(frame: Frame, file: ByteArray, pos: Boxed<Int>): Int {
        if (frame !is Frame.Binary) return -1
        val bytes = frame.readBytes()
        for (i in bytes.indices) {
            file[pos.value] = bytes[i]
            pos.value += 1
        }
        return bytes.size
    }

    private fun getMetadata(it: Frame, fingerprint: Box<String?>?, len: Boxed<Int>?) {
        if (it !is Frame.Text) return
        val initialization = Json.decodeFromString<VerificationInitialization>(it.readText())
        fingerprint?.value = initialization.fingerprint
        len?.value = initialization.length
    }

    private fun CoroutineScope.verifyFile(documentId: String, channel: Channel<Message>, fingerprint: String?, file: ByteArray) {
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
    }
}
