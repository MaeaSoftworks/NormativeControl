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
import io.ktor.server.routing.*
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.app.web.utlis.header
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.domain.services.StudentsService
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Security
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.withRoles
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.*
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NoAccessException
import java.time.Instant

object StudentsController : ControllerModule() {
    override fun Routing.register() {
        route("/student") {
            authenticate(Security.JWT.CONFIGURATION_NAME) {
                header("Authorization") {
                    withRoles(Role.STUDENT) {
                        route("/document") {
                            webSocket("/verify") {
                                StudentsService.Verification {
                                    userId = call.authentication.principal<JWTPrincipal>()!!.subject!!
                                    onVerificationEnded = {
                                        transaction { DocumentRepository.save(Document(documentId, userId!!, Instant.now())) }
                                        launch {
                                            channel.receiveAsFlow().map { Frame.Text(it.toString()) }.collect(::send)
                                            close()
                                        }
                                    }
                                }.launch()
                            }

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
                    StudentsService.Verification {
                        onVerificationEnded = {
                            launch {
                                channel.receiveAsFlow().map { Frame.Text(it.toString()) }.collect(::send)
                                close()
                            }
                        }
                    }.launch()
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
