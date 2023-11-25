package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.flow.Flow
import org.springframework.http.codec.multipart.FilePart
import java.nio.ByteBuffer

interface S3AsyncStorage {
    suspend fun putObjectAsync(body: FilePart, objectName: String, tags: Map<String, String>)

    fun getTagsAsync(objectName: String): Flow<Map<String, String>>

    fun getObjectAsync(objectName: String): Flow<ByteBuffer>
}
