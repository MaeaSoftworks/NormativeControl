package ru.maeasoftworks.normativecontrol.core.utils

import kotlinx.coroutines.CoroutineName
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import kotlin.coroutines.coroutineContext

suspend fun getVerificationContext() = coroutineContext[VerificationContext.Key]

suspend inline fun <T> verificationContext(fn: VerificationContext.() -> T): T {
    return getVerificationContext()?.fn() ?: throw RuntimeException("Verification context was not found in coroutine ${coroutineContext[CoroutineName]?.name}")
}