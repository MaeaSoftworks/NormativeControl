package ru.maeasoftworks.normativecontrol.api.shared.modules

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import ru.maeasoftworks.normativecontrol.api.shared.utils.Module

object CORS: Module {
    override fun Application.module() {
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.Authorization)
            anyHost()
        }
    }
}