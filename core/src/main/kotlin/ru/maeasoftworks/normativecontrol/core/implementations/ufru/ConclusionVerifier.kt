package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface ConclusionVerifier<T> : Chapter<T> {
    suspend fun verifyForConclusion(element: T)

    companion object Companion : Chapter.Companion
}