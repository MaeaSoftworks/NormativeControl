package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ru.maeasoftworks.normativecontrol.shared.exceptions.AuthenticationException
import ru.maeasoftworks.normativecontrol.shared.exceptions.NoAccessException
import ru.maeasoftworks.normativecontrol.shared.exceptions.StatusException

inline fun <reified T : StatusException> StatusPagesConfig.registerException() {
    exception<T> { call, cause ->
        call.respondText(cause.message, ContentType.Application.Any, cause.code)
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        registerException<NoAccessException>()
        registerException<AuthenticationException>()

        exception<Throwable> { call, cause ->
            call.respondText(text = "Unregistered exception: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}