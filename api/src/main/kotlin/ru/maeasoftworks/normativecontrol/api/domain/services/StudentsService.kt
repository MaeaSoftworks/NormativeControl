package ru.maeasoftworks.normativecontrol.api.domain.services

import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.maeasoftworks.normativecontrol.api.app.web.dto.DocumentListResponse
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.app.web.dto.VerificationInitialization
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Box
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Boxed
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import java.io.ByteArrayInputStream

object StudentsService {
    fun fillFile(frame: Frame, file: ByteArray, pos: Boxed<Int>): Int {
        if (frame !is Frame.Binary) return -1
        val bytes = frame.readBytes()
        for (i in bytes.indices) {
            file[pos.value] = bytes[i]
            pos.value += 1
        }
        return bytes.size
    }

    fun getMetadata(it: Frame, fingerprint: Box<String?>?, len: Boxed<Int>?) {
        if (it !is Frame.Text) return
        val initialization = Json.decodeFromString<VerificationInitialization>(it.readText())
        fingerprint?.value = initialization.fingerprint
        len?.value = initialization.length
    }

    fun CoroutineScope.verifyFile(documentId: String, channel: Channel<Message>, fingerprint: String?, file: ByteArray) {
        val uploading = launch { FileStorage.uploadSourceDocument(documentId, file, fingerprint) }
        val verification = launch { VerificationService.startVerification(documentId, fingerprint, ByteArrayInputStream(file), channel) }
        channel.invokeOnClose {
            if (uploading.isActive) {
                uploading.cancel()
            }
            if (verification.isActive) {
                verification.cancel()
            }
        }
    }

    suspend fun getDocumentsByUser(userId: String) = transaction {
        DocumentRepository.getAllByUserId(userId).map { DocumentListResponse(it) }
    }
}