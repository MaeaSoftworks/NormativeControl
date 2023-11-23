package ru.maeasoftworks.normativecontrol.exceptions

import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KClass

open class StatusException(val code: HttpStatusCode, override val message: String): Throwable(message)

class IncorrectFileException(message: String) : Exception(message)

class NoAccessException: StatusException(HttpStatusCode.Forbidden, "You don't have access to this document")