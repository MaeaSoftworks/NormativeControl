package ru.maeasoftworks.normativecontrol.api.shared.modules

import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface FileStorage {
    suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>)

    suspend fun getTags(objectName: String): Map<String, String>?

    suspend fun getObject(objectName: String): Flow<ByteBuffer>

    companion object: FileStorage {
        private lateinit var instance: FileStorage

        @JvmStatic
        fun initialize(storage: FileStorage) {
            instance = storage
        }

        override suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>) = instance.putObject(file, objectName, tags)

        override suspend fun getTags(objectName: String): Map<String, String>? = instance.getTags(objectName)

        override suspend fun getObject(objectName: String): Flow<ByteBuffer> = instance.getObject(objectName)
    }
}