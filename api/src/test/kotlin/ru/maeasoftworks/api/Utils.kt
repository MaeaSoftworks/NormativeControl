package ru.maeasoftworks.api

import io.ktor.server.application.Application
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

fun Application.applyModule(module: Module) {
    this.apply {
        module.apply {
            module()
        }
    }
}

fun Application.applyModules(vararg module: Module) {
    this.apply {
        module.forEach {
            it.apply {
                module()
            }
        }
    }
}

inline fun <T : Module> Application.configuredModule(module: T, config: T.() -> Unit) {
    this.apply {
        module.apply {
            module()
        }
        module.config()
    }
}