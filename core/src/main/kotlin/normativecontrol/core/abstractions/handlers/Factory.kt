package normativecontrol.core.abstractions.handlers

import normativecontrol.core.annotations.HandlerFactory

/**
 * Base interface for handlers' factories.
 * Inheritors should be:
 * - companion object of [Handler] inheritor;
 * - marked with [HandlerFactory] to be initialized.
 * @param T type of handler
 */
internal interface Factory<T> {
    /**
     * Creates new instance of handler of type [T].
     * @return new instance of handler of type [T]
     */
    fun create(): T
}