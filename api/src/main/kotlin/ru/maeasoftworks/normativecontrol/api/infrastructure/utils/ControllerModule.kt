package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

abstract class ControllerModule : Module {
    abstract fun Routing.register()

    override fun Application.module() {
        routing {
            register()
        }
    }

    @Suppress("FunctionName")
    protected fun Application.super_module() {
        module()
    }
}