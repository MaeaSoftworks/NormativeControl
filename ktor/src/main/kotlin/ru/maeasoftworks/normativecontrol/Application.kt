package ru.maeasoftworks.normativecontrol

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import org.kodein.di.DI
import org.kodein.di.Instance
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.type.jvmType
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.inspectors.initializeInspectorModule
import ru.maeasoftworks.normativecontrol.shared.initializeSharedModule
import ru.maeasoftworks.normativecontrol.shared.modules.configureHTTP
import ru.maeasoftworks.normativecontrol.shared.modules.configureSerialization
import ru.maeasoftworks.normativecontrol.shared.modules.configureStatusPages
import ru.maeasoftworks.normativecontrol.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.students.initializeStudentModule

const val variablePath = "ktor.profile"

fun main(args: Array<String>) {
    val profile = System.getenv()[variablePath]
    if (profile == null) {
        println("Environment variable `$variablePath` is not set. Please, provide correct `$variablePath` value in environment.")
        return
    }
    EngineMain.main(args + "-config=application-$profile.yaml")
}

fun Application.default() = setupDI {
    //plugins
    configureHTTP()
    configureSerialization()
    configureStatusPages()

    //shared - warning: do not move it below!
    initializeSharedModule(this@default)

    initializeStudentModule()
    initializeInspectorModule()
}

fun Application.setupDI(mapper: DI.MainBuilder.(Application) -> Unit = {}) {
    val di = DI {
        bind<Application>() with instance(this@setupDI)
        mapper(this, this@setupDI)
    }

    routing {
        for (bind in di.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && Controller::class.java.isAssignableFrom(bindClass)) {
                val res by di.Instance(bind.key.type)
                val logger = LoggerFactory.getLogger(bindClass)
                logger.info("Registering '$res' routes...")
                (res as Controller).apply { registerRoutes() }
            }
        }
    }
}