package ru.maeasoftworks.normativecontrol.core.abstractions

interface Chapter<T> {
    suspend fun verify(element: T)

    interface Companion
}