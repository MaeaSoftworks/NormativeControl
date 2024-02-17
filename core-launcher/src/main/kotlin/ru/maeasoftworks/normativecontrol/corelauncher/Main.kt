package ru.maeasoftworks.normativecontrol.corelauncher

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.runBlocking
import ru.maeasoftworks.normativecontrol.core.Document
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.configurations.VerificationConfiguration
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import java.io.File

fun main(): Unit = runBlocking {
    VerificationConfiguration.initialize {
        forceStyleInlining = false
    }

    val ctx = VerificationContext(Profile.UrFU)
    Document(ctx).apply {
        load(File("core-launcher/src/main/resources/ignore/different sized parts.docx").inputStream())
        runVerification()
    }
    HttpClient().use { client ->
        client.post("http://localhost:8081/set") {
            setBody(ctx.render.getString())
        }
    }
}