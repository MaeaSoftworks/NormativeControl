package ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage

import io.ktor.server.application.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import java.nio.ByteBuffer

object InMemoryFileStorage: Module, FileStorage {
    private val storage: MutableMap<String, Pair<ByteArray, Map<String, String>>> = mutableMapOf()

    override fun Application.module() {
        FileStorage.initialize(InMemoryFileStorage)
    }

    override suspend fun putObject(file: ByteArray, objectName: String, vararg tags: Pair<String, String>) {
        storage[objectName] = Pair(file, tags.toMap())
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