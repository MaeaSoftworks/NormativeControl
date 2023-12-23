package ru.maeasoftworks.normativecontrol.api.shared.modules

import io.ktor.server.application.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer

object InMemoryFileStorage: FileStorage {
    private val storage: MutableMap<String, Pair<ByteArray, Map<String, String>>> = mutableMapOf()

    override fun Application.internalInitialize() { }

    override suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>) {
        storage[objectName] = Pair(file, tags)
    }

    override suspend fun getTags(objectName: String): Map<String, String>? {
        return storage[objectName]?.second
    }

    override suspend fun getObject(objectName: String): Flow<ByteBuffer> {
        val file = storage[objectName]?.first ?: return emptyFlow()
        return flow {
            emit(ByteBuffer.wrap(file))
        }
    }
}