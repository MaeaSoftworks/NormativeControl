package normativecontrol.core

import normativecontrol.core.configurations.HandlerCollection
import normativecontrol.core.handlers.Handler
import normativecontrol.core.data.Result
import normativecontrol.core.data.Statistics
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.configurations.AbstractHandlerCollection
import normativecontrol.core.predefined.Predefined
import normativecontrol.core.utils.LogColor
import normativecontrol.core.utils.highlight
import normativecontrol.shared.debug
import normativecontrol.shared.error
import normativecontrol.shared.timer
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InvalidObjectException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Entrypoint to core library. Scans package `normativecontrol`
 * on initialization and build handlers mappings.
 */
object Core {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val configurations = mutableMapOf<String, () -> AbstractHandlerCollection>()

    init {
        val packageName = "normativecontrol"
        logger.debug { "Searching handler factories at '$packageName'..." }
        timer({ logger.debug { "Searching done in $it ms" } }) {
            val configNames = mutableMapOf<KClass<*>, String>()

            val reflections = Reflections(packageName)
            reflections.getTypesAnnotatedWith(HandlerCollection::class.java)
                .map { it.kotlin }
                .forEach { configuration ->
                    val setup = configuration.findAnnotation<HandlerCollection>()!!
                    configNames += configuration to setup.name
                    configurations += setup.name to
                            (configuration.constructors.firstOrNull()
                                ?: throw InvalidObjectException("Configuration class should have only primary constructor without args")
                                    ).let { { it.call() as AbstractHandlerCollection } }
                }

            reflections.getTypesAnnotatedWith(Handler::class.java)
                .map { it.kotlin }
                .forEach { handlerClass ->
                    try {
                        val handlerAnnotation = handlerClass.findAnnotation<Handler>()!!
                        val configName = configNames[handlerAnnotation.configuration]!!
                        if (configName == Predefined.NAME) {
                            Runtime.predefinedFactories[handlerAnnotation.handledElementType] =
                                handlerAnnotation.priority to (handlerClass.constructors.find { it.parameters.isEmpty() }?.let { { it.call() as AbstractHandler<*> } }
                                    ?: throw InvalidObjectException("Handler class should have only primary constructor without args"))
                        } else {
                            if (!Runtime.factories.containsKey(configName)) {
                                Runtime.factories[configName] = mutableMapOf()
                            }
                            Runtime.factories[configName]!![handlerAnnotation.handledElementType] =
                                handlerClass.constructors.find { it.parameters.isEmpty() }?.let { { it.call() as AbstractHandler<*> } }
                                    ?: throw InvalidObjectException("Handler class should have only primary constructor without args")
                        }
                        logger.debug {
                            handlerClass.simpleName!!.highlight(LogColor.ANSI_YELLOW) + " loaded to group " +
                                    configName.highlight(generateColor(configName))
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Unable to load handler ${handlerClass.qualifiedName}." }
                    }
                }
        }
    }

    private fun generateColor(value: String): LogColor {
        return LogColor.entries[value.hashCode() % LogColor.entries.count()]
    }

    /**
     * Verifies [source] file.
     * @param source [InputStream] of file
     * @param configurationName name of configuration that will be applied to document verification
     * @return results as [Result] object
     */
    fun verify(source: InputStream, configurationName: String): Result {
        val runtime = timer({ logger.debug { "Runtime initialization done in $it ms" } }) {
            Runtime(
                configurationName,
                configurations
            )
        }
        val document = timer({ logger.debug { "Unpacking: $it ms" } }) {
            Document(runtime, source)
        }
        timer({ logger.debug { "Verification: $it ms" } }) {
            document.runVerification()
        }
        return timer({ logger.debug { "Saving: $it ms" } }) {
             Result(
                ByteArrayOutputStream().also { document.writeResult(it) },
                document.render,
                Statistics(
                    document.mistakeCount
                )
            )
        }
    }
}