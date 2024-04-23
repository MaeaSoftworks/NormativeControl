package normativecontrol.core.handlers

import normativecontrol.core.annotations.HandlerGroup
import kotlin.reflect.KClass

/**
 * Base class for handler groups. For correct registration in [HandlerMapper]
 * should be annotated with [HandlerGroup].
 * @constructor creates a new handler collection with specified name.
 * @param name name of group
 */
abstract class HandlerCollection(val name: String) {
    /**
     * Map of instances of handlers. Should not be modified manually.
     */
    internal val instances = mutableMapOf<KClass<*>, Handler<*>>()
}