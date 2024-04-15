package normativecontrol.core.handlers

import kotlin.reflect.KClass

object HandlerMapper {
    const val PREDEFINED_NAME = "__PREDEFINED"

    val factories = mutableMapOf<String, MutableMap<KClass<*>, Factory<*>>>()

    init {
        factories[PREDEFINED_NAME] = mutableMapOf()
    }

    operator fun get(collection: HandlerCollection, target: Any): Handler<*>? {
        val targetClass = target::class
        if (collection.instances.containsKey(targetClass))
            return collection.instances[targetClass]

        if (!factories.containsKey(collection.name)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        val factory = factories[collection.name]!![target::class]
        if (factory == null) {
            val handler = factories[PREDEFINED_NAME]!![target::class]?.create() as? Handler<*>
            if (handler != null) {
                collection.instances[targetClass] = handler
                return handler
            }
        }

        val instance = factory?.create() as? Handler<*>
        if (instance != null) {
            collection.instances[targetClass] = instance
            return instance
        }
        return null
    }
}