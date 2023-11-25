package ru.maeasoftworks.normativecontrol.api.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> CompletableFuture<T>.await(): T = suspendCoroutine { cont: Continuation<T> ->
    whenComplete { result, exception ->
        if (exception == null) cont.resume(result) else cont.resumeWithException(exception)
    }
}

fun <T> CompletableFuture<T>.asFlow(): Flow<T> {
    return flow { emit(await()) }
}

// region Mock of kotlinx-coroutines internal API for compatibility
private suspend inline fun <T> Flow<T>.collectWhile(crossinline predicate: suspend (value: T) -> Boolean) {
    val collector = object : FlowCollector<T> {
        override suspend fun emit(value: T) {
            if (!predicate(value)) {
                throw AbortFlowException(this)
            }
        }
    }
    try {
        collect(collector)
    } catch (e: AbortFlowException) {
        e.checkOwnership(collector)
    }
}

class AbortFlowException(private val owner: FlowCollector<*>) : CancellationException("Flow was aborted, no more elements needed") {
    fun checkOwnership(owner: FlowCollector<*>) {
        if (this.owner !== owner) throw this
    }
}
// endregion

fun <T> Flow<T>.takeWhileInclusive(predicate: suspend (T) -> Boolean): Flow<T> = flow {
    var isEnded = false
    return@flow collectWhile { value ->
        if (predicate(value)) {
            emit(value)
            true
        } else {
            if (!isEnded) {
                isEnded = true
                emit(value)
            }
            false
        }
    }
}
