package ru.maeasoftworks.normativecontrol.api.shared.utils

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

abstract class ControllerModule: Module {
    abstract fun Routing.register()

    final override fun Application.module() {
        routing {
            register()
        }
    }
}