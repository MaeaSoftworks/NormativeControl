package normativecontrol.core

import java.lang.reflect.Method

class Call<T : Any>(private val instance: T, private val method: Method) {
    operator fun invoke(vararg args: Any): Any {
        return method.invoke(instance, *args)
    }
}

inline operator fun <T : Any> T.invoke(fn: () -> String): Call<T> {
    return Call(this, this::class.java.declaredMethods.first { it.name == fn() }.apply { isAccessible = true })
}