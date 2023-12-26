package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respondText
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object StatusPages: Module {
    override fun Application.module() {
        install(StatusPages) {
            registerException<NoAccessException>()
            registerException<AuthenticationException>()
            registerException<OutdatedRefreshToken>()
            registerException<InvalidRefreshToken>()

            exception<Throwable> { call, cause ->
                call.respondText(text = "Unregistered exception: $cause", status = HttpStatusCode.InternalServerError)
            }
        }
    }

    private inline fun <reified T : StatusException> StatusPagesConfig.registerException() {
        exception<T> { call, cause ->
            call.respondText(cause.message, ContentType.Application.Any, cause.code)
        }
    }
}