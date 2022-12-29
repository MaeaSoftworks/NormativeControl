package com.maeasoftworks.polonium.utils

/**
 * Execute block of code until exception E is caught
 * @param E type of exception
 * @param body block of code
 */
inline fun <reified E : Exception> doUntilCatch(body: () -> Unit) {
    try {
        body()
    } catch (e: Exception) {
        if (e !is E) {
            throw e
        }
    }
}
