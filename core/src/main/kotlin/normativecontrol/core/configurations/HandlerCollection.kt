package normativecontrol.core.configurations

/**
 * Automatic scan target annotation. All classes annotated by
 * this annotation should:
 * 1. be an inheritor of [AbstractConfiguration] or [AbstractHandlerCollection];
 * 2. have only primary constructor without args.
 *
 * @param name name of collection. Should be same as [AbstractHandlerCollection.name]
 * @sample normativecontrol.implementation.urfu.UrFUConfiguration
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class HandlerCollection(
    val name: String
)
