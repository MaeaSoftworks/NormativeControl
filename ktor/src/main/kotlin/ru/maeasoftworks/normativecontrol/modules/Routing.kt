package ru.maeasoftworks.normativecontrol.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.maeasoftworks.normativecontrol.routers.studentRouter

fun Application.configureRouting() {
    routing {
        studentRouter()
    }
}