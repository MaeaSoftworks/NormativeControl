package ru.maeasoftworks.normativecontrol.shared.utils

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class Extractor(private val multiPartData: MultiPartData) {
    private val parts = mutableMapOf<String, PartData>()

    val String.string: String
        get() = (parts[this]!! as PartData.FormItem).value

    val String.byteArray: ByteArray
        get() = (parts[this]!! as PartData.FileItem).streamProvider().readAllBytes()

    internal suspend fun <T> execute(fn: Extractor.() -> T): T = coroutineScope {
        multiPartData.forEachPart {
            parts[it.name!!] = it
        }
        return@coroutineScope async { fn() }.await()
    }
}

suspend fun <T> ApplicationCall.extractMultipartParts(fn: Extractor.() -> T): T {
    return Extractor(this.receiveMultipart()).execute(fn)
}