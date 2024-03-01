package ru.maeasoftworks.normativecontrol.corelauncher

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.runBlocking
import ru.maeasoftworks.normativecontrol.core.Document
import ru.maeasoftworks.normativecontrol.core.configurations.VerificationConfiguration
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun main(): Unit = runBlocking {

    VerificationConfiguration.initialize {
        forceStyleInlining = false
    }

    val ctx = VerificationContext(UrFUProfile)
    Document(ctx).apply {
        load(File("core-launcher/src/main/resources/ignore/sample2.docx").inputStream())
        runVerification()
        val stream = ByteArrayOutputStream()
        writeResult(stream)
        FileOutputStream("core-launcher/src/main/resources/ignore/latest-result.docx").use {
            stream.writeTo(it)
        }
    }
    HttpClient().use { client ->
        client.post("http://localhost:8081/set") {
            setBody(ctx.render.getString())
        }
    }
}