package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.conclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.render
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Jwt
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule

object InspectorViewController: ControllerModule() {
    override fun Routing.register() {
        route("/inspector") {
            authenticate(Jwt.CONFIGURATION_NAME) {
                route("/document") {
                    get("/conclusion") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val filename = conclusion(documentId)
                        call.respondBytesWriter(ContentType.defaultForFileExtension("docx"), HttpStatusCode.OK) {
                            FileStorage.getObject(filename).collect {
                                this.writeFully(it)
                            }
                        }
                    }

                    get("/render") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
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
}