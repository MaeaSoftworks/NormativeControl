package com.maeasoftworks.docx4nc.utils

inline fun <reified E : Exception> ignore(body: () -> Unit) {
    try {
        body()
    } catch (e: Exception) {
        if (e !is E) {
            throw e
        }
    }
}