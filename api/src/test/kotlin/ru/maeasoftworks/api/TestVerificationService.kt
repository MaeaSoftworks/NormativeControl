package ru.maeasoftworks.api

import io.ktor.server.application.Application
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadDocumentConclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadDocumentRender
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import java.io.InputStream

object TestVerificationService: VerificationService, Module {
    override fun Application.module() {
        VerificationService.initialize(TestVerificationService)
    }

    override suspend fun startVerification(
        documentId: String,
        fingerprint: String?,
        file: InputStream,
        channel: Channel<Message>?,
        profile: Profile
    ): Unit = coroutineScope {
        var stage = Message.Stage.INITIALIZATION
        val task = launch {
            delay(500)
            stage = Message.Stage.VERIFICATION
            delay(2000)
            stage = Message.Stage.SAVING
            FileStorage.uploadDocumentRender(documentId, file.readAllBytes(), fingerprint)
            FileStorage.uploadDocumentConclusion(documentId, file.readBytes(), fingerprint)
        }
        while (task.isActive) {
            delay(200)
            channel?.send(Message.Progress(-1.0, stage))
        }
        task.invokeOnCompletion {
            launch {
                channel?.send(Message.Success(documentId))
                channel?.close()
            }
        }
    }
}