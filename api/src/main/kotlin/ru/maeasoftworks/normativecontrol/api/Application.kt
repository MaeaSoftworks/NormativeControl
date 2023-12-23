package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.app.Profile
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.inspectorAccountRouting
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.inspectorViewRouting
import ru.maeasoftworks.normativecontrol.api.shared.modules.JWTService.configureJWT
import ru.maeasoftworks.normativecontrol.api.shared.modules.S3.configureS3
import ru.maeasoftworks.normativecontrol.api.shared.modules.configureHTTP
import ru.maeasoftworks.normativecontrol.api.shared.modules.configureSerialization
import ru.maeasoftworks.normativecontrol.api.shared.modules.configureStatusPages
import ru.maeasoftworks.normativecontrol.api.shared.services.Database.configureDatabase
import ru.maeasoftworks.normativecontrol.api.shared.services.RefreshTokenService.configureRefreshTokenService
import ru.maeasoftworks.normativecontrol.api.students.controllers.studentsRouting
import ru.maeasoftworks.normativecontrol.core.HotLoader

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Application::main")
    var profile = Profile(System.getenv()[Profile.ARGUMENT_NAME])
    if (profile == null) {
        logger.warn("Environment variable '${Profile.ARGUMENT_NAME}' is not set. Force 'production' profile...")
        profile = Profile.PRODUCTION
    } else {
        logger.info("Set environment profile: '$profile'")
    }
    EngineMain.main(args + "-config=application-$profile.yaml")
}

fun Application.initialize() {
    run common@{
        configureDatabase()
        configureS3()
        configureHTTP()
        configureJWT()
        configureRefreshTokenService()
        configureSerialization()
        configureStatusPages()
    }
    run students@{
        HotLoader.load()
        routing {
            studentsRouting()
        }
    }
    run inspectors@{
        routing {
            inspectorAccountRouting()
            inspectorViewRouting()
        }
    }
}
