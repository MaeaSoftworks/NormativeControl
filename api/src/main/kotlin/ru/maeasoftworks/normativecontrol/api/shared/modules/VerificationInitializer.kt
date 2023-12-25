package ru.maeasoftworks.normativecontrol.api.shared.modules

import io.ktor.server.application.Application
import ru.maeasoftworks.normativecontrol.api.shared.utils.Module
import ru.maeasoftworks.normativecontrol.core.HotLoader

object VerificationInitializer: Module {
    override fun Application.module() {
        HotLoader.load()
    }
}