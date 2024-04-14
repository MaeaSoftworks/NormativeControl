package normativecontrol.core.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class HandlerFactory(
    val target: KClass<*>,
    val configuration: KClass<*>
)