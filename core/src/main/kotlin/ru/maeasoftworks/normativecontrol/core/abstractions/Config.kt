package ru.maeasoftworks.normativecontrol.core.abstractions

class Config<T, C: State?>(
    val handler: () -> Handler<T, *>,
    val test: (target: Any) -> Boolean,
    val state: (() -> C)?,
    val profile: Profile
) {
    companion object {
        inline fun <T, C: State?> create(builder: ConfigBuilder<T, C>.() -> Unit): Config<T, C> {
            return ConfigBuilder<T, C>().also(builder).build()
        }
    }

    class ConfigBuilder<T, C: State?> {
        var test: ((target: Any) -> Boolean)? = null
        private var handler: (() -> Handler<T, *>)? = null
        private var state: (() -> C)? = null
        private var profile: Profile? = null

        inline fun <reified T> setTarget() {
            test = { it is T }
        }

        fun setState(fn: () -> C) {
            state = fn
        }

        fun <H: Handler<T, *>> setHandler(fn: () -> H) {
            handler = fn
        }

        fun setProfile(profile: Profile) {
            this.profile = profile
        }

        fun build(): Config<T, C> {
            return Config(
                handler ?: throw NullPointerException("Handler should be not null"),
                test ?: throw NullPointerException("Target should be not null"),
                state,
                profile ?: throw NullPointerException("Profile should be not null")
            )
        }
    }
}