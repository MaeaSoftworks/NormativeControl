package com.maeasoftworks.tellurium.documentation

/**
 * Annotation that marks class as class with documentation in `message.properties` file.
 *
 * If class has `@RestController` [DocumentationCreator] will generate documentation only for methods.
 *
 * If not, data class documentation will be generated.
 * @see DocumentationCreator
 * @author prmncr
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Documentation