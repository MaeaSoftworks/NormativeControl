package ru.maeasoftworks.normativecontrol

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.jvmType
import ru.maeasoftworks.normativecontrol.shared.modules.*
import ru.maeasoftworks.normativecontrol.shared.utils.Controller
import ru.maeasoftworks.normativecontrol.students.initializeStudentModule

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() = setupKodein {
    bindSingleton { Database(it) }
    bindSingleton { JWTService(it) }
    bindSingleton { S3(it) }
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    initializeStudentModule(it)
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