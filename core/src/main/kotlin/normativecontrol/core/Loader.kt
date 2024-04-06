package normativecontrol.core

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.annotations.Handler
import normativecontrol.core.annotations.StateFactory
import normativecontrol.core.utils.timer
import normativecontrol.shared.debug
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

object Loader {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val nothing = Nothing::class

    init {
        val packageName = this::class.java.`package`.name
        logger.debug { "Searching handlers at '$packageName'..." }
        timer({ logger.debug { "Handlers' initialization done in $it ms" } }) {
            val handlers = Reflections(packageName).getTypesAnnotatedWith(Handler::class.java)
            handlers.forEach { it.kotlin.objectInstance as? AbstractHandler }
            handlers.forEach {
                val handler = it.kotlin.findAnnotations(Handler::class).firstOrNull() ?: return@forEach
                val config = handler.configuration.objectInstance as Configuration
                val state = if (handler.state == nothing) null else handler.state.companionObjectInstance as? State.Key
                HandlerMapper.add(config, handler.target, it.kotlin.objectInstance as AbstractHandler)
                logger.debug { "${handler.configuration.simpleName}: ${it.kotlin.simpleName!!}" }
                val prop = it.kotlin.superclasses.first().memberProperties.find { prop -> prop.hasAnnotation<StateFactory>() }
                if (prop !is KMutableProperty<*>) return@forEach
                prop.isAccessible = true
                prop.setter.call(it.kotlin.objectInstance, state)
            }
        }
    }
}