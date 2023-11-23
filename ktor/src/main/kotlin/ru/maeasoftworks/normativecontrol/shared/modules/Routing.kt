package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.maeasoftworks.normativecontrol.inspectors.routers.inspectorRouter
import ru.maeasoftworks.normativecontrol.students.routers.studentRouter

fun Application.configureRouting() {
    routing {
        studentRouter()
        inspectorRouter()
    }
}