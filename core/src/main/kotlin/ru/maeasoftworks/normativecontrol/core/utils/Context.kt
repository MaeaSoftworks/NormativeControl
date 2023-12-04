package ru.maeasoftworks.normativecontrol.core.utils

import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import kotlin.coroutines.coroutineContext

suspend fun getContext() = coroutineContext[VerificationContext.Key]

suspend inline fun <T> usingContext(fn: (VerificationContext) -> T): T {
    return fn(getContext()!!)
}