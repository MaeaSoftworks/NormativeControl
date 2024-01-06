package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.respond
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Event
import ru.maeasoftworks.normativecontrol.api.app.web.dto.VerificationInitialization
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.MultipartExtractor.Companion.extractMultipartParts
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.WebSockets
import java.io.ByteArrayInputStream
import kotlin.math.ceil

object StudentsController: ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            route("anonymous") {
                route("/document") {
                    webSocket("/upload") {
                        var isReadyToUpload = false
                        var fingerprint: String? = null
                        var len: Int? = null
                        var file: ByteArray? = null
                        var pos = 0
                        incoming.receiveAsFlow().collect {
                            if (!isReadyToUpload) {
                                if (it !is Frame.Text) return@collect
                                val initialization = Json.decodeFromString<VerificationInitialization>(it.readText())
                                fingerprint = initialization.fingerprint
                                len = initialization.length
                                send("Upload was initialized. Waiting for ${ceil(len!! * 1.0 / WebSockets.maxFrameSize).toInt()} file frames...")
                                isReadyToUpload = true
                                return@collect
                            } else if (pos < len!!) {
                                if (it !is Frame.Binary) return@collect
                                file = ByteArray(len!!)
                                val bytes = it.readBytes()
                                for (i in bytes.indices) {
                                    file!![pos] = bytes[i]
                                    pos++
                                }
                                send(Frame.Text("Added ${bytes.size} bytes; received $pos of $len bytes."))
                            }
                            if (pos == len!!) {
                                send("All data received.")
                                val channel = Channel<Message>(Channel.UNLIMITED)
                                verifyFile(channel, fingerprint!!, file!!)
                                launch {
                                    channel.receiveAsFlow()
                                        .map { message -> Frame.Text(message.toString()) }
                                        .collect { i -> send(i) }
                                    close()
                                }
                            }
                        }
                    }

                    post("/upload") {
                        val file = call.extractMultipartParts { file("file") }
                        val fingerprint = call.request.cookies["fingerprint"]
                            ?: KeyGenerator.generate(64).also {
                                call.response.cookies.append(Cookie("fingerprint", value = it, path = "/student/anonymous"))
                            }
                        val channel = Channel<Message>(Channel.UNLIMITED)
                        verifyFile(channel, fingerprint, file)
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

    private fun CoroutineScope.verifyFile(channel: Channel<Message>, fingerprint: String, file: ByteArray) {
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
    }
}
