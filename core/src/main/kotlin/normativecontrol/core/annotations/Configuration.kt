package normativecontrol.core.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Configuration(
    val name: String
)
