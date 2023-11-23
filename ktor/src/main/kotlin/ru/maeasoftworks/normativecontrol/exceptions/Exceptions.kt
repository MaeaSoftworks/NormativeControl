package ru.maeasoftworks.normativecontrol.exceptions

import io.ktor.http.content.*
import kotlin.reflect.KClass

class IncorrectFileException(message: String) : Exception(message)

class InvalidPartDataTypeException(partData: PartData, expectedType: KClass<*>) :
    Exception("Part '${partData.name}' was in incorrect type. Expected: ${expectedType.simpleName}, actual: ${partData::class.simpleName}")

class RequiredPartNotFoundException() : Exception("Inconsistent form-data parts: expected")