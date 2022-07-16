package com.maeasoftworks.docx4nc.utils

inline fun <reified E : Exception> doUntilCatch(body: () -> Unit) {
    try {
        body()
    } catch (e: Exception) {
        if (e !is E) {
            throw e
        }
    }
}