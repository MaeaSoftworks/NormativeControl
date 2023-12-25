package ru.maeasoftworks.normativecontrol.api.shared.modules

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import ru.maeasoftworks.normativecontrol.api.shared.utils.Module

object Serialization: Module {
    override fun Application.module() {
        install(ContentNegotiation) {
            json()
        }
    }
}