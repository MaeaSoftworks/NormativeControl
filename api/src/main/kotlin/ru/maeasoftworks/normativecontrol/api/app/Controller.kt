package ru.maeasoftworks.normativecontrol.api.app

import io.ktor.server.routing.Routing

abstract class Controller {
    abstract fun Routing.registerRoutes()
}