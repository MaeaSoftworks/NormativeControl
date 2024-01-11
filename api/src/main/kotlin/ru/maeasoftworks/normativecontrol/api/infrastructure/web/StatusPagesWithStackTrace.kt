package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import dev.reformator.stacktracedecoroutinator.runtime.DecoroutinatorRuntime
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.util.logging.error
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object StatusPagesWithStackTrace : Module {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun Application.module() {
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                DecoroutinatorRuntime.load()
                call.respondText(
                    text = "Exception: ${cause.stackTraceToString()}",
                    status = HttpStatusCode.InternalServerError
                )
                logger.error(cause)
            }
        }
    }
}