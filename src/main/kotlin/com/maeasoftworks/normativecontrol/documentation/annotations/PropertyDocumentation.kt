package com.maeasoftworks.normativecontrol.documentation.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PropertyDocumentation(val description: String, val enum: KClass<*> = Unit::class)
