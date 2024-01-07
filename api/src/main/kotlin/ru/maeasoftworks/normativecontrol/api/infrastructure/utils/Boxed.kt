package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

class Boxed<T : Any> : Box<T> {
    val isInitialized: Boolean
        get() = valueUnsafe != null

    private var valueUnsafe: T? = null

    override var value: T
        get() = valueUnsafe!!
        set(value) {
            valueUnsafe = value
        }

    constructor()

    constructor(value: T) {
        this.value = value
    }

    class Nullable<T> : Box<T?> {
        var isInitialized: Boolean = false
            private set

        private var valueUnsafe: T? = null

        override var value: T?
            get() = valueUnsafe
            set(value) {
                valueUnsafe = value
                isInitialized = true
            }

        constructor()

        constructor(value: T) {
            this.value = value
        }
    }
}