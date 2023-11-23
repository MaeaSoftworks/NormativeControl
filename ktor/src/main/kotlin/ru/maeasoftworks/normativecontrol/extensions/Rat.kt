package ru.maeasoftworks.normativecontrol.extensions

class Rat<T1, T2>(private val path: (T1) -> T2) {
    var target: T1? = null
    fun report(): T2? {
        return target?.let { path(it) }
    }
}

infix fun <T1, T2> T1.with(rat: Rat<T1, T2>): T1 {
    rat.target = this
    return this
}