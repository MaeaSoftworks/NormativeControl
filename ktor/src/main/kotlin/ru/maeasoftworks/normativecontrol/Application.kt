package ru.maeasoftworks.normativecontrol

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.routing
import org.kodein.di.DI
import org.kodein.di.Instance
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.type.jvmType
import ru.maeasoftworks.normativecontrol.inspectors.initializeInspectorModule
import ru.maeasoftworks.normativecontrol.shared.initializeSharedModule
import ru.maeasoftworks.normativecontrol.shared.modules.configureHTTP
import ru.maeasoftworks.normativecontrol.shared.modules.configureSerialization
import ru.maeasoftworks.normativecontrol.shared.modules.configureStatusPages
import ru.maeasoftworks.normativecontrol.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.students.initializeStudentModule

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.default() = setupKodein {
    //plugins
    configureHTTP()
    configureSerialization()
    configureStatusPages()

    //shared - warning: do not move below!
    initializeSharedModule(it)

    initializeStudentModule(it)
    initializeInspectorModule(it)
}

fun Application.setupKodein(
    kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}
) {
    val kodein = DI {
        bind<Application>() with instance(this@setupKodein)
        kodeinMapper(this, this@setupKodein)
    }

    routing {
        for (bind in kodein.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && Controller::class.java.isAssignableFrom(bindClass)) {
                val res by kodein.Instance(bind.key.type)
                println("Registering '$res' routes...")
                (res as Controller).apply { registerRoutes() }
            }
        }
    }
}