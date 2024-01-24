package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.app.Profile

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Application::main")
    val profileArg = try {
        Profile.ARGUMENT_NAMES.mapNotNull { System.getenv()[it] }.last()
    } catch (e: NoSuchElementException) {
        Profile.ARGUMENT_NAMES.map { System.getProperty(it) }.last()
    }
    var profile = Profile(profileArg)
    if (profile == null) {
        logger.warn("Any of environment variables '${Profile.ARGUMENT_NAMES}' is not set. Force 'production' profile...")
        profile = Profile.PRODUCTION
    } else {
        logger.info("Set environment profile: '$profile'")
    }
    EngineMain.main(args + "-config=application-$profile.conf")
}