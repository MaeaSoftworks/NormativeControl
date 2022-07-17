package com.maeasoftworks.docx4nc.utils

/**
 * Выполнить функцию в блоке try/catch
 *
 * @author prmncr
 */
inline fun <reified E : Exception> ignore(body: () -> Unit) {
    try {
        body()
    } catch (e: Exception) {
        if (e !is E) {
            throw e
        }
    }
}