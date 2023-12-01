package ru.maeasoftworks.normativecontrol.api.app

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import javax.inject.Inject

class ControllerInitializer @Inject constructor(private val controllers: Set<@JvmSuppressWildcards Controller>) {
    fun Application.register() {
        routing {
            controllers.forEach { controller ->
                controller.apply { registerRoutes() }
            }
        }
    }
}