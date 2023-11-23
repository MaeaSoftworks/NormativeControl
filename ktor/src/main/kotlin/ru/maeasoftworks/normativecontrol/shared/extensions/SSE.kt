package ru.maeasoftworks.normativecontrol.shared.extensions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend inline fun <reified T> ApplicationCall.respond(eventFlow: Flow<@Serializable T>) {
    response.cacheControl(CacheControl.NoCache(null))
    response.status(HttpStatusCode.OK)
    respondBytesWriter(contentType = ContentType.Text.EventStream) {
        eventFlow.collect { event ->
            val data = Json.encodeToString(event)
            for (dataLine in data.lines()) {
                writeStringUtf8("data: $dataLine\n")
            }
            writeStringUtf8("\n")
            flush()
        }
    }
}

suspend inline fun <reified T> ApplicationCall.respond(eventChannel: Channel<@Serializable T>) = respond(eventChannel.receiveAsFlow())