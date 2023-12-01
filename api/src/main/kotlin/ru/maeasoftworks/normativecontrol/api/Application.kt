package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.app.DaggerApplicationConfiguration
import ru.maeasoftworks.normativecontrol.api.app.initializeApplication

private const val variablePath = "ktor.profile"

private val logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    var profile = System.getenv()[variablePath]
    if (profile == null) {
        logger.warn("Environment variable '$variablePath' is not set. Forcing 'production' profile...")
        profile = "production"
    } else {
        logger.info("Set environment profile: '$profile'")
    }
    EngineMain.main(args + "-config=application-$profile.yaml")
}

fun Application.allModules() = initializeApplication(DaggerApplicationConfiguration::builder)
