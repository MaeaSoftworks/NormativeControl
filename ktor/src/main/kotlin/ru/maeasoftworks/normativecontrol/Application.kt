package ru.maeasoftworks.normativecontrol

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.modules.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    launch { configureDatabase() }
    configureS3()
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureRouting()
}
