package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface FrontPageVerifier<T> : Chapter<T> {
    suspend fun verifyForFrontPage(element: T)

    companion object Companion : Chapter.Companion
}