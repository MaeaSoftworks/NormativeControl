package ru.maeasoftworks.normativecontrol.core.abstractions

interface Mapping<T> {
    fun handler(): Handler<T>
    fun test(target: Any): Boolean

    companion object {
        inline fun <reified T> of(crossinline handler: () -> Handler<T>): Mapping<T> {
            return object : Mapping<T> {
                override fun handler(): Handler<T> {
                    return handler()
                }

                override fun test(target: Any): Boolean {
                    return target is T
                }
            }
        }
    }
}