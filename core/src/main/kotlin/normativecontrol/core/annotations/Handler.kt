package normativecontrol.core.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Handler(
    val target: KClass<*>,
    val configuration: KClass<*>,
    val state: KClass<*> = Nothing::class
)