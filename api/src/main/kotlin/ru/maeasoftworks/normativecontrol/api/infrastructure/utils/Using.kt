package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

inline fun <T, TResult> using(obj: T, fn: T.() -> TResult): TResult {
    return obj.fn()
}