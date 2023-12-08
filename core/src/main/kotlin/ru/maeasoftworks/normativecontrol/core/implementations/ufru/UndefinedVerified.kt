package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter

interface UndefinedVerified<T> : Chapter<T> {
    fun verifyForUndefinedChapter(element: T) {}

    companion object Companion : Chapter.Companion
}