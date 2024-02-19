package ru.maeasoftworks.normativecontrol.api.infrastructure.core

import io.ktor.server.application.Application
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.core.configurations.VerificationConfiguration

object CoreConfiguration : Module {
    override fun Application.module() {
        VerificationConfiguration.initialize {
            forceStyleInlining = environment.config.property("core.forceStyleInlining").getString().toBoolean()
        }
    }
}