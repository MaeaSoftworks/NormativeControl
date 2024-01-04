package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.cacheControl
import io.ktor.server.response.respondBytesWriter
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.flow.Flow
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Event

suspend inline fun ApplicationCall.respond(events: Flow<Event>) {
    response.cacheControl(CacheControl.NoCache(null))
    response.status(HttpStatusCode.OK)
    respondBytesWriter(contentType = ContentType.Text.EventStream) {
        events.collect { event ->
            if (this.isClosedForWrite) {
                return@collect
            }
            writeStringUtf8(event.toString())
            flush()
        }
    }
}