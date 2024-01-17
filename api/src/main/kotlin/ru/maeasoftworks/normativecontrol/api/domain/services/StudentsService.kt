package ru.maeasoftworks.normativecontrol.api.domain.services

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.app.web.dto.DocumentListResponse
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.VerificationInitialization
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRequestException
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.WebSockets
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import java.io.ByteArrayInputStream
import kotlin.math.ceil

object StudentsService {
    suspend fun verifyFile(
        scope: CoroutineScope,
        documentId: String,
        file: ByteArray,
        channel: Channel<Message>,
        userId: String? = null,
        fingerprint: String? = null
    ) {
        if (userId == null && fingerprint == null) {
            throw InvalidRequestException()
        }
        val profile = if (fingerprint == null) transaction { UserRepository.identify(userId!!) }.organization.profile else Profile.UrFU
        val uploading = scope.launch { FileStorage.uploadSourceDocument(documentId, file, fingerprint) }
        val verification = scope.launch { VerificationService.startVerification(documentId, fingerprint, ByteArrayInputStream(file), channel, profile) }
        channel.invokeOnClose {
            if (uploading.isActive) {
                uploading.cancel()
            }
            if (verification.isActive) {
                verification.cancel()
            }
        }
    }

    context(Transaction)
    suspend fun getDocumentsByUser(userId: String) = DocumentRepository.getAllByUserId(userId).map { DocumentListResponse(it) }

    context(WebSocketSession)
    suspend inline fun verification(verification: VerificationBuilder.() -> Unit) {
        val builder = VerificationBuilder()
        verification(builder)
        builder.apply {
            launch()
        }
    }

    class VerificationBuilder {
        private var fingerprint: String? = null
        var userId: String? = null
        var onVerificationEnded: (suspend VerificationBuilder.() -> Unit)? = null
        val channel: Channel<Message> = Channel(Channel.UNLIMITED)
        lateinit var documentId: String

        context(WebSocketSession) @PublishedApi internal suspend fun launch(): Unit = withContext(coroutineContext) {
            var len = -1
            var file: ByteArray? = null
            var pos = 0
            incoming.receiveAsFlow().collect { frame ->
                if (len == -1) {
                    if (frame !is Frame.Text) return@collect
                    val initialization = Json.decodeFromString<VerificationInitialization>(frame.readText())
                    fingerprint = initialization.fingerprint
                    len = initialization.length
                    if (len < 0) throw InvalidRequestException("'length' must be more that 0")
                    file = ByteArray(len)
                    send("Upload was initialized. Waiting for ${ceil(len * 1.0 / WebSockets.maxFrameSize).toInt()} file frames...")
                    return@collect
                }
                if (pos < len) {
                    if (frame !is Frame.Binary) return@collect
                    val bytes = frame.readBytes()
                    for (i in bytes.indices) {
                        file!![pos] = bytes[i]
                        pos += 1
                    }
                    val added = bytes.size
                    send(Frame.Text("Added $added bytes; received $pos of $len bytes."))
                }
                if (pos == len) {
                    send("All data received.")
                    documentId = KeyGenerator.generate(32)
                    if (fingerprint != null) {
                        verifyFile(this, documentId, file!!, channel, fingerprint = fingerprint)
                    } else if (userId != null) {
                        verifyFile(this, documentId, file!!, channel, userId)
                    }
                    onVerificationEnded?.invoke(this@VerificationBuilder)
                }
            }
        }
    }
}