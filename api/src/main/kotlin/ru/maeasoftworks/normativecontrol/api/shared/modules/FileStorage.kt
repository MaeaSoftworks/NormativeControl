package ru.maeasoftworks.normativecontrol.api.shared.modules

import io.ktor.server.application.Application
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface FileStorage {
    fun Application.internalInitialize()

    suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>)

    suspend fun getTags(objectName: String): Map<String, String>?

    suspend fun getObject(objectName: String): Flow<ByteBuffer>

    companion object: FileStorage {
        private lateinit var instance: FileStorage

        fun initialize(application: Application, storage: FileStorage) {
            instance = storage
            instance.apply {
                application.apply {
                    internalInitialize()
                }
            }
        }

        override fun Application.internalInitialize() = throw UnsupportedOperationException("Interface cannot be initialized.")

        override suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>) = instance.putObject(file, objectName, tags)

        override suspend fun getTags(objectName: String): Map<String, String>? = instance.getTags(objectName)

        override suspend fun getObject(objectName: String): Flow<ByteBuffer> = instance.getObject(objectName)
    }
}