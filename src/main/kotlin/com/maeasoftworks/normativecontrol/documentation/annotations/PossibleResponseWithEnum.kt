package com.maeasoftworks.normativecontrol.documentation.annotations

import org.springframework.http.HttpStatus
import kotlin.reflect.KClass

@Repeatable
annotation class PossibleResponseWithEnum(
    val httpStatus: HttpStatus,
    val enum: KClass<*>,
    val type: KClass<*> = Unit::class,
    val description: String = "",
    val body: String = ""
)