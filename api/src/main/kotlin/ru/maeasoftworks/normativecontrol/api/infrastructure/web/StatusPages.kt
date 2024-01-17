package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText
import io.ktor.util.logging.error
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object StatusPages : Module {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun Application.module() {
        install(StatusPages) {
            registerException<NoAccessException>()
            registerException<AuthenticationException>()
            registerException<OutdatedException>()
            registerException<InvalidRefreshToken>()
            registerException<CredentialsIsAlreadyInUseException>()
            registerException<EntityNotFoundException>()
            registerException<InconsistentStateException>()
            registerException<InvalidRequestException>()
            registerException<NotFoundException>()
            registerException<NotApplicableException>()
            registerException<IdentificationException>()

            exception<Throwable> { call, cause ->
                call.respondText(text = "Unregistered exception: $cause", status = HttpStatusCode.InternalServerError)
                logger.error(cause)
            }
        }
    }

    private inline fun <reified T : StatusException> StatusPagesConfig.registerException() {
        exception<T> { call, cause ->
            call.respondText(cause.message, ContentType.Text.Plain, cause.code)
        }
        exception<RequestValidationException> { call, cause ->
            call.respondText(cause.reasons.joinToString(), ContentType.Text.Plain, HttpStatusCode.BadRequest)
        }
    }
}