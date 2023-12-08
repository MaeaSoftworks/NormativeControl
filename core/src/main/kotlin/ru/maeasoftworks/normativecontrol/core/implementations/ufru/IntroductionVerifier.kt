package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface IntroductionVerifier<T> : Chapter<T> {
    suspend fun verifyForIntroduction(element: T)

    companion object Companion : Chapter.Companion
}