package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface ReferencesVerifier<T> : Chapter<T> {
    fun verifyForReferences(element: T)

    companion object Companion : Chapter.Companion
}