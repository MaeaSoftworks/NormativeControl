package normativecontrol.core

import normativecontrol.core.abstractions.HandlerCollection
import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.annotations.Handler
import normativecontrol.core.annotations.StateFactoryBind
import normativecontrol.core.utils.timer
import normativecontrol.shared.debug
import normativecontrol.shared.error
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.io.InvalidClassException
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

object Loader {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val packageName = this::class.java.`package`.name
        logger.debug { "Searching handlers at '$packageName'..." }
        val loaded = mutableMapOf<String, MutableList<String>>()
        timer({ logger.debug { "Handlers' initialization done in $it ms" } }) {
            Reflections(packageName)
                .getTypesAnnotatedWith(Handler::class.java)
                .map { it.kotlin }
                .forEach { handlerClass ->
                    try {
                        val handlerAnnotation = handlerClass.findAnnotation<Handler>()!!
                        val config = handlerAnnotation.configuration.objectInstance as HandlerCollection
                        val state = handlerAnnotation.state.companionObjectInstance as? StateFactory
                        HandlerMapper.add(config, handlerAnnotation.target, handlerClass.objectInstance as AbstractHandler)
                        val prop = handlerClass.superclasses.find { it == AbstractHandler::class }?.memberProperties?.find { it.hasAnnotation<StateFactoryBind>() }
                            ?: throw InvalidClassException("Handler should extends ${AbstractHandler::class.qualifiedName}")
                        if (prop !is KMutableProperty<*>) return@forEach
                        prop.isAccessible = true
                        prop.setter.call(handlerClass.objectInstance, state)
                        if (!loaded.containsKey(handlerAnnotation.configuration.simpleName)) {
                            loaded[handlerAnnotation.configuration.simpleName!!] = mutableListOf()
                        }
                        loaded[handlerAnnotation.configuration.simpleName!!]!! += handlerClass.simpleName!!
                    } catch (e: Exception) {
                        logger.error(e) { "Unable to load handler ${handlerClass.qualifiedName}." }
                    }
                }
        }
        if (logger.isDebugEnabled) {
            logger.debug { "Loaded handlers:" }
            loaded.forEach { (key, value) ->
                logger.debug { "$key: [${value.joinToString()}]" }
            }
        }
    }
}