package normativecontrol.core.utils

import kotlin.reflect.KProperty

class LateInitReadonlyDelegate<V>(defaultValue: V? = null) {
    private var initialized = false
    private var value: V? = defaultValue

    operator fun <T : Any> getValue(target: T, property: KProperty<*>): V {
        return value!!
    }

    operator fun <T : Any> setValue(target: T, property: KProperty<*>, value: V) {
        if (initialized) {
            throw IllegalStateException("This property can be initialized only once.")
        }
        this.value = value
        initialized = true
    }
}

fun <V> lateinitVal(defaultValue: V? = null) = LateInitReadonlyDelegate(defaultValue)