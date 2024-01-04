package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.util.asStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MultipartExtractor(val multiPartData: MultiPartData) {
    val parts = mutableMapOf<String, PartData>()

    fun string(path: String): String = (parts[path]!! as PartData.FormItem).value

    suspend fun file(path: String): ByteArray {
        return withContext(Dispatchers.IO) {
            (parts[path]!! as PartData.FileItem).provider().asStream().readAllBytes()
        }
    }

    companion object {
        suspend inline fun <T> ApplicationCall.extractMultipartParts(fn: MultipartExtractor.() -> T): T {
            return MultipartExtractor(this.receiveMultipart()).run {
                multiPartData.forEachPart {
                    parts[it.name!!] = it
                }
                fn()
            }
        }
    }
}