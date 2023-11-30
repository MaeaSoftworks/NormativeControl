package ru.maeasoftworks.normativecontrol.api.inspectors.controllers

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.api.shared.extensions.conclusion
import ru.maeasoftworks.normativecontrol.api.shared.extensions.render
import ru.maeasoftworks.normativecontrol.api.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.api.shared.services.DocumentService

class InspectorViewController(override val di: DI) : Controller() {
    private val documentService: DocumentService by instance()

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
                            null,
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
                            null,
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