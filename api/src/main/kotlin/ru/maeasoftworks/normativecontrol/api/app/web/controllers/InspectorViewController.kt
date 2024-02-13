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
import kotlinx.coroutines.flow.map
import ru.maeasoftworks.normativecontrol.api.app.web.dto.DocumentListResponse
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.services.AccountService
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
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRequestException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.NotApplicableException

object InspectorViewController : ControllerModule() {

    override fun Routing.register() {
        route("/inspector") {
            authenticate(Security.JWT.CONFIGURATION_NAME) {
                withRoles(Role.INSPECTOR) {
                    route("/document") {
                        get("/conclusion") {
                            val documentId = call.parameters["documentId"] ?: throw InvalidRequestException("documentId must be not null")
                            transaction {
                                AccountService.shouldBeInSameOrganization(
                                    UserRepository.identify(call.authentication.principal<JWTPrincipal>()!!.subject!!),
                                    DocumentRepository.getUserByDocumentId(documentId)
                                )
                            }
                            val filename = conclusion(documentId)
                            call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                                FileStorage.getObject(filename).collect {
                                    this.writeFully(it)
                                }
                            }
                        }

                        get("/render") {
                            val documentId = call.parameters["documentId"] ?: throw InvalidRequestException("documentId must be not null")
                            val filename = render(documentId)
                            call.respondBytesWriter(ContentType.defaultForFileExtension("html"), HttpStatusCode.OK) {
                                FileStorage.getObject(filename).collect {
                                    this.writeFully(it)
                                }
                            }
                        }
                    }

                    route("/students") {
                        get("/find") {
                            val email = call.request.queryParameters["email"] ?: throw InvalidRequestException("email must be not null")
                            transaction {
                                var targetUser: User?
                                AccountService.shouldBeInSameOrganization(
                                    UserRepository.identify(call.authentication.principal<JWTPrincipal>()!!.subject!!),
                                    UserRepository.getUserByEmail(email).also { targetUser = it }
                                )
                                if (targetUser?.roles?.contains(Role.STUDENT) != true) throw NotApplicableException("User is not a student")
                                call.respond(DocumentRepository.getAllByUserId(targetUser!!.id).map { DocumentListResponse(it) })
                            }
                        }
                    }
                }
            }
        }
    }
}