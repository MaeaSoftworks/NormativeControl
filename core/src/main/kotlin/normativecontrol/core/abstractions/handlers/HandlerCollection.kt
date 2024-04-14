package normativecontrol.core.abstractions.handlers

import kotlin.reflect.KClass

abstract class HandlerCollection(val name: String) {
    val instances = mutableMapOf<KClass<*>, Handler<*>>()
}