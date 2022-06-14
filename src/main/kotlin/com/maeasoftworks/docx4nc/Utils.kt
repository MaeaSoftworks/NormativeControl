package com.maeasoftworks.docx4nc

inline fun <reified E : Exception> ignoring(body: () -> Unit) {
    try {
        body()
    } catch (e: Exception) {
        if (e !is E) {
            throw e
        }
    }
}
