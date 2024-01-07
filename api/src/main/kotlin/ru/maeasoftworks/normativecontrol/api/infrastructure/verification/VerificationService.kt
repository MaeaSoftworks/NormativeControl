package ru.maeasoftworks.normativecontrol.api.infrastructure.verification

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.FileStorage
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadDocumentConclusion
import ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage.uploadDocumentRender
import ru.maeasoftworks.normativecontrol.core.Document
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

object VerificationService {
    suspend fun startVerification(
        documentId: String,
        fingerprint: String?,
        file: InputStream,
        channel: Channel<Message>? = null,
        profile: Profile = Profile.UrFU
    ) = coroutineScope {
        var stage = Message.Stage.INITIALIZATION
        val ctx = VerificationContext(profile)
        val document = Document(ctx)
        val task = launch {
            withContext(ctx) {
                withContext(Dispatchers.IO) { document.load(file) }

                stage = Message.Stage.VERIFICATION
                document.runVerification()
                stage = Message.Stage.SAVING
                val result = withContext(Dispatchers.IO) { ByteArrayOutputStream().also { document.writeResult(it) } }

                FileStorage.uploadDocumentRender(documentId, document.ctx.render.getString().toByteArray(), fingerprint)
                FileStorage.uploadDocumentConclusion(documentId, result.toByteArray(), fingerprint)
            }
        }
        while (task.isActive) {
            delay(200)
            val progress = (ctx.ptr.bodyPosition * 1.0 / ctx.ptr.totalChildSize).let { if (it.isNaN()) 0.0 else it }
            channel?.send(Message.Progress(progress, stage))
        }
        task.invokeOnCompletion {
            launch {
                channel?.send(Message.Success(documentId))
                channel?.close()
            }
        }
    }
}