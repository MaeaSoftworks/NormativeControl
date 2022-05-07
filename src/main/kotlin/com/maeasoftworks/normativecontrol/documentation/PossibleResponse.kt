package com.maeasoftworks.normativecontrol.documentation

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

@Repeatable
annotation class PossibleResponse(
    val httpStatus: HttpStatus,
    val type: KClass<*> = Unit::class,
    val description: String = "",
    val body: String = ""
)