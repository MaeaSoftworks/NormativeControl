package normativecontrol.core.configurations

import normativecontrol.core.Runtime

/**
 * Base class for collections of handlers. Used for mapping handlers to groups.
 * For correct registration in [Runtime] should be annotated with [HandlerCollection].
 * @constructor creates a new handler collection with specified name.
 * @property name Name of collection
 */
abstract class AbstractHandlerCollection(val name: String)