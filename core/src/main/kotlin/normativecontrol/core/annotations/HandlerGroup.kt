package normativecontrol.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class HandlerGroup(
    val name: String
)
