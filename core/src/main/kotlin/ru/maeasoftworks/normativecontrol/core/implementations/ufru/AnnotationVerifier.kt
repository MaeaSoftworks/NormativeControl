package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface AnnotationVerifier<T> : Chapter<T> {
    suspend fun verifyForAnnotation(element: T)

    companion object Companion : Chapter.Companion
}