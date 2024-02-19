package ru.maeasoftworks.normativecontrol.renderingtestserver

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8081, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    configureCORS()
    configureWebSockets()
    configureRouting()
}
