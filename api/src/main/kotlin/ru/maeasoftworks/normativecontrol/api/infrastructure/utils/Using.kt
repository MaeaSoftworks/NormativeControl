package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

fun <T, TResult> using(obj: T, fn: T.() -> TResult): TResult {
    return obj.fn()
}