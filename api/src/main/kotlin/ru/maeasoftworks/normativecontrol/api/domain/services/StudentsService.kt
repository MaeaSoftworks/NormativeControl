package ru.maeasoftworks.normativecontrol.api.domain.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.maeasoftworks.normativecontrol.api.app.web.dto.DocumentListResponse
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.DocumentRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadSourceDocument
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRequestException
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import java.io.ByteArrayInputStream

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
        val profile = if (fingerprint == null) transaction { UserRepository.identify(userId!!) }.organization.profile else UrFUProfile
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
}