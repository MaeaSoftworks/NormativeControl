package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.VerificationInitialization
import ru.maeasoftworks.normativecontrol.api.app.web.utlis.header
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.domain.services.StudentsService
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Security
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.withRoles
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.identify
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRequestException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.WebSockets
import java.time.Instant
import kotlin.math.ceil

object StudentsController : ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            authenticate(Security.JWT.CONFIGURATION_NAME) {
                header("Authorization") {
                    withRoles(Role.STUDENT) {
                        route("/document") {
                            get("/list") {
                                transaction {
                                    call.authentication.identify()
                                    call.respond(StudentsService.getDocumentsByUser(call.authentication.principal<JWTPrincipal>()!!.subject!!))
                                }
                            }

                            get("/conclusion") {
                                val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                                val isOwner = transaction {
                                    val user = call.authentication.identify()
                                    DocumentRepository.isUserOwnerOf(user.id, documentId)
                                }
                                if (!isOwner) throw NoAccessException()
                                val filename = conclusion(documentId)
                                call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                                    FileStorage.getObject(filename).collect {
                                        this.writeFully(it)
                                    }
                                }
                            }

                            get("/render") {
                                val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                                val isOwner = transaction {
                                    val user = UserRepository.identify(call.authentication.principal<JWTPrincipal>()!!.subject!!)
                                    DocumentRepository.isUserOwnerOf(user.id, documentId)
                                }
                                if (!isOwner) throw NoAccessException()
                                val filename = render(documentId)
                                call.respondBytesWriter(ContentType.defaultForFileExtension("html"), HttpStatusCode.OK) {
                                    FileStorage.getObject(filename).collect {
                                        this.writeFully(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            route("/document") {
                webSocket("/verify") {
                    var fingerprint: String? = null
                    var userId: String? = null
                    val channel: Channel<Message> = Channel(Channel.UNLIMITED)
                    lateinit var documentId: String
                    var len = -1
                    var file: ByteArray? = null
                    var pos = 0
                    incoming.receiveAsFlow().collect { frame ->
                        if (len == -1) {
                            if (frame !is Frame.Text) return@collect
                            val initialization = Json.decodeFromString<VerificationInitialization>(frame.readText())
                            if (initialization.token != null) {
                                try {
                                    userId = Security.JWT.authenticateJwt(initialization.token)?.subject
                                } catch (e: Exception) { //todo more details
                                    channel.close()
                                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Cannot authorize this token"))
                                    return@collect
                                }
                            }
                            fingerprint = initialization.fingerprint
                            len = initialization.length
                            if (len < 0) throw InvalidRequestException("'length' must be more that 0")
                            file = ByteArray(len)
                            send("Upload was initialized. Waiting for ${ceil(len * 1.0 / WebSockets.maxFrameSize).toInt()} file frames...")
                            return@collect
                        }
                        if (pos < len) {
                            if (frame !is Frame.Binary) return@collect
                            val bytes = frame.readBytes()
                            for (i in bytes.indices) {
                                file!![pos] = bytes[i]
                                pos += 1
                            }
                            val added = bytes.size
                            send(Frame.Text("Added $added bytes; received $pos of $len bytes."))
                        }
                        if (pos == len) {
                            send("All data received.")
                            documentId = KeyGenerator.generate(32)
                            if (fingerprint != null) {
                                StudentsService.verifyFile(this, documentId, file!!, channel, fingerprint = fingerprint)
                            } else if (userId != null) {
                                StudentsService.verifyFile(this, documentId, file!!, channel, userId)
                            }
                            if (userId != null) {
                                launch {
                                    transaction { DocumentRepository.save(Document(documentId, userId!!, Instant.now())) }
                                }
                            }
                            launch {
                                channel.receiveAsFlow().map { Frame.Text(it.toString()) }.collect(::send)
                                close()
                            }
                        }
                    }
                }

                get("/conclusion") {
                    val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                    val fingerprint = call.parameters["fingerprint"] ?: throw IllegalArgumentException("fingerprint must be not null")
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
                    val fingerprint = call.parameters["fingerprint"] ?: throw IllegalArgumentException("fingerprint must be not null")
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
