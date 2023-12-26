package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class Extractor(private val multiPartData: MultiPartData) {
    private val parts = mutableMapOf<String, PartData>()

    fun String.string(): String = (parts[this]!! as PartData.FormItem).value

    suspend fun String.file(): ByteArray = coroutineScope {
        return@coroutineScope withContext(Dispatchers.IO) {
            (parts[this@file]!! as PartData.FileItem).streamProvider().readAllBytes()
        }
    }

    internal suspend fun <T> execute(fn: suspend Extractor.() -> T): T {
        multiPartData.forEachPart {
            parts[it.name!!] = it
        }
        return fn()
    }
}

suspend fun <T> ApplicationCall.extractMultipartParts(fn: suspend Extractor.() -> T): T {
    return Extractor(this.receiveMultipart()).execute(fn)
}