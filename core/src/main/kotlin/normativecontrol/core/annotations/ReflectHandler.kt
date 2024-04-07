package normativecontrol.core.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReflectHandler(
    val target: KClass<*>,
    val configuration: KClass<*>
)