package ru.maeasoftworks.normativecontrol.api.infrastructure.verification

import kotlinx.coroutines.channels.Channel
import ru.maeasoftworks.normativecontrol.api.app.web.dto.Message
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import java.io.InputStream

interface VerificationService {
    suspend fun startVerification(
        documentId: String,
        fingerprint: String?,
        file: InputStream,
        channel: Channel<Message>? = null,
        profile: Profile = Profile.UrFU
    )

    companion object: VerificationService {
        private lateinit var instance: VerificationService

        @JvmStatic
        fun initialize(instance: VerificationService) {
            this.instance = instance
        }

        override suspend fun startVerification(documentId: String, fingerprint: String?, file: InputStream, channel: Channel<Message>?, profile: Profile) {
            instance.startVerification(documentId, fingerprint, file, channel, profile)
        }
    }
}