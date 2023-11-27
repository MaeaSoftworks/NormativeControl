package ru.maeasoftworks.normativecontrol.inspectors.controllers

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*
import org.kodein.di.DI
import ru.maeasoftworks.normativecontrol.shared.utils.Controller

class InspectorViewController(override val di: DI) : Controller() {
    override fun Routing.registerRoutes() {
        route("/inspector") {
            authenticate("jwt") {
                route("/document") {
                    get("/render") {

                    }
                    get("/conclusion") {

                    }
                }
            }
        }
    }
}