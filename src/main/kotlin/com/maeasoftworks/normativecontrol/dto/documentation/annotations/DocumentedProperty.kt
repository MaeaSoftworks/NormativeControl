package com.maeasoftworks.normativecontrol.dto.documentation.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocumentedProperty(val translationId: String, val enum: KClass<*> = Unit::class)
