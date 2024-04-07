package normativecontrol.core

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.handlers.HandlerCollection
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.utils.timer
import normativecontrol.shared.debug
import normativecontrol.shared.error
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.reflect.full.*

object Core {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val packageName = this::class.java.`package`.name
        logger.debug { "Searching handlers at '$packageName'..." }
        val loaded = mutableMapOf<String, MutableList<String>>()
        timer({ logger.debug { "Handlers' initialization done in $it ms" } }) {
            Reflections(packageName)
                .getTypesAnnotatedWith(ReflectHandler::class.java)
                .map { it.kotlin }
                .forEach { handlerClass ->
                    try {
                        val reflectHandlerAnnotation = handlerClass.findAnnotation<ReflectHandler>()!!
                        val config = reflectHandlerAnnotation.configuration.objectInstance as HandlerCollection
                        HandlerMapper.add(config, reflectHandlerAnnotation.target, handlerClass.objectInstance as Handler<*>)
                        if (!loaded.containsKey(reflectHandlerAnnotation.configuration.simpleName)) {
                            loaded[reflectHandlerAnnotation.configuration.simpleName!!] = mutableListOf()
                        }
                        loaded[reflectHandlerAnnotation.configuration.simpleName!!]!! += handlerClass.simpleName!!
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

    fun verify(source: InputStream, configuration: Configuration<*>): Pair<ByteArrayOutputStream, String> {
        Document(configuration).apply {
            load(source)
            runVerification()
            return ByteArrayOutputStream().also { writeResult(it) } to this.ctx.render.getString()
        }
    }
}