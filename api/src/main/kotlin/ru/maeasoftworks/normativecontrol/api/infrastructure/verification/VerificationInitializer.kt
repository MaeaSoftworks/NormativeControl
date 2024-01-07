package ru.maeasoftworks.normativecontrol.api.infrastructure.verification

import io.ktor.server.application.Application
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.core.HotLoader

object VerificationInitializer : Module {
    override fun Application.module() {
        HotLoader.load()
    }
}