package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface AppendixVerifier<T> : Chapter<T> {
    suspend fun verifyForAppendix(element: T)

    companion object Companion : Chapter.Companion
}