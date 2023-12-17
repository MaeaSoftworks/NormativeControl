package ru.maeasoftworks.normativecontrol.api.inspectors.controllers

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import ru.maeasoftworks.normativecontrol.api.app.Controller
import ru.maeasoftworks.normativecontrol.api.shared.extensions.conclusion
import ru.maeasoftworks.normativecontrol.api.shared.extensions.render
import ru.maeasoftworks.normativecontrol.api.shared.services.DocumentService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InspectorViewController @Inject constructor(
    private val documentService: DocumentService
) : Controller() {
    override fun Routing.registerRoutes() {
        route("/inspector") {
            authenticate("jwt") {
                route("/document") {
                    get("/conclusion") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val filename = conclusion(documentId)

                        call.respondBytesWriter(
                            ContentType.defaultForFileExtension("docx"),
                            HttpStatusCode.OK,
                            null
                        ) {
                            documentService.getFileUnsafe(filename).collect {
                                this.writeFully(it)
                            }
                        }
                    }

                    get("/render") {
                        val documentId = call.parameters["documentId"] ?: throw IllegalArgumentException("documentId must be not null")
                        val filename = render(documentId)

                        call.respondBytesWriter(
                            ContentType.defaultForFileExtension("html"),
                            HttpStatusCode.OK,
                            null
                        ) {
                            documentService.getFileUnsafe(filename).collect {
                                this.writeFully(it)
                            }
                        }
                    }
                }
            }
        }
    }
}