package normativecontrol.core.handlers

import normativecontrol.core.Runtime
import normativecontrol.core.annotations.Configuration
import kotlin.reflect.KClass

/**
 * Base class for handler groups. For correct registration in [Runtime]
 * should be annotated with [Configuration].
 * @constructor creates a new handler collection with specified name.
 * @param name name of group
 */
abstract class HandlerCollection(val name: String) {
    /**
     * Map of instances of handlers. Should not be modified manually.
     */
    internal val instances = mutableMapOf<KClass<*>, AbstractHandler<*>>()

    val handlerInstances: Map<KClass<*>, AbstractHandler<*>>
        get() = instances
}