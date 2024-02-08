package ru.maeasoftworks.normativecontrol.core.abstractions

/**
 * Attempt to create restrictions to all classes that not need to use this field.
 * Should be used as public field's context.
 * All classes that need access to field should have object that implements this interface (**not a companion object**).
 * @sample Handler.Proxy
 */
interface ProxyContext