package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.app.Profile

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Application::main")
    var profile = Profile(System.getenv()[Profile.ARGUMENT_NAME] ?: System.getProperty(Profile.ARGUMENT_NAME))
    if (profile == null) {
        logger.warn("Environment variable '${Profile.ARGUMENT_NAME}' is not set. Force 'production' profile...")
        profile = Profile.PRODUCTION
    } else {
        logger.info("Set environment profile: '$profile'")
    }
    EngineMain.main(args + "-config=application-$profile.conf")
}