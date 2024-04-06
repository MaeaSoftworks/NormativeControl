package normativecontrol.core

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.utils.timer
import normativecontrol.shared.debug
import org.reflections.Reflections
import org.slf4j.LoggerFactory

object Loader {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val packageName = this::class.java.`package`.name
        logger.debug("Searching handlers at '$packageName'...")
        var instances: List<Handler<*, *>>? = null
        timer({ logger.debug { "Search done in $it ms" } }) {
            val initialized = Reflections(packageName).getTypesAnnotatedWith(EagerInitialization::class.java)
            instances = initialized.mapNotNull { it.kotlin.objectInstance as? Handler<*, *> }
        }
        if (logger.isDebugEnabled) {
            val groups = instances!!
                .groupBy { it.handlerConfig.configuration }
                .map { (key, value) ->
                    "${key::class.simpleName!!}: " + value.joinToString { it::class.simpleName!! }
                }
            logger.debug { "Loaded handlers:" }
            groups.forEach {
                logger.debug { it }
            }
        }
    }
}