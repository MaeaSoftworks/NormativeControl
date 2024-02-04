package ru.maeasoftworks.normativecontrol.core.abstractions

interface Mapping<T> {
    fun handler(): Handler<T>
    fun test(target: Any): Boolean
}

inline fun <reified S> mapping(noinline handler: () -> Handler<S>): Mapping<S> {
    return object : Mapping<S> {
        override fun handler(): Handler<S> {
            return handler()
        }
        override fun test(target: Any): Boolean {
            return target is S
        }
    }
}