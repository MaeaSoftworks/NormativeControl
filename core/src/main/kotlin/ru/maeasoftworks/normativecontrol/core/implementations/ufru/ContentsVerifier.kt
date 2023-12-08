package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface ContentsVerifier<T> : Chapter<T> {
    suspend fun verifyForContents(element: T)

    companion object Companion : Chapter.Companion
}