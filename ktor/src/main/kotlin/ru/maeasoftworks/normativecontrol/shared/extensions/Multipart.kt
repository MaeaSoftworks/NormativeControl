package ru.maeasoftworks.normativecontrol.shared.extensions

import io.ktor.http.content.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend inline fun <reified T> MultiPartData.extractParts(): T = coroutineScope cs@{
    var instance: T? = null
    async {
        val clazz = T::class
        if (!clazz.isData) {
            throw Exception("Provided type is not data class")
        }
        val ctor = clazz.constructors.first()
        val paramNamesList = ctor.parameters.map { it.name }
        val args = Array<Any?>(paramNamesList.size) { null }
        var i = 0
        this@extractParts.forEachPart { part ->
            args[i++] = if (paramNamesList.contains(part.name!!)) {
                when (part) {
                    is PartData.FormItem -> part.value
                    is PartData.FileItem -> part.streamProvider().readAllBytes()
                    else -> null
                }
            } else null
        }
        instance = ctor.call(*args)
    }.await()
    return@cs instance!!
}