package ru.maeasoftworks.normativecontrol.api.students.components

import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.ByteBuffer

interface S3AsyncStorage {
    fun putObjectAsync(body: FilePart, objectName: String, tags: Map<String, String>): Mono<Boolean>

    fun getTagsAsync(objectName: String): Mono<Map<String, String>>

    fun getObjectAsync(objectName: String): Flux<ByteBuffer>
}