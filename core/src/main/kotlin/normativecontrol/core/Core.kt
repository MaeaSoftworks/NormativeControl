package normativecontrol.core

import normativecontrol.core.annotations.Handler
import normativecontrol.core.annotations.Configuration
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.HandlerCollection
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

object Core {
    const val PREDEFINED_NAME = "__PREDEFINED"

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val configurations = mutableMapOf<String, () -> HandlerCollection>()

    init {
        val packageName = "normativecontrol"
        logger.debug { "Searching handler factories at '$packageName'..." }
        timer({ logger.debug { "Searching done in $it ms" } }) {
            val configNames = mutableMapOf<KClass<*>, String>()

            val reflections = Reflections(packageName)
            reflections.getTypesAnnotatedWith(Configuration::class.java)
                .map { it.kotlin }
                .forEach { configuration ->
                    val setup = configuration.findAnnotation<Configuration>()!!
                    configNames += configuration to setup.name
                    configurations += setup.name to
                        (configuration.constructors.firstOrNull()
                            ?: throw InvalidObjectException("Configuration class should have only primary constructor without args")
                        ).let { { it.call() as HandlerCollection } }
                }

            reflections.getTypesAnnotatedWith(Handler::class.java)
                .map { it.kotlin }
                .forEach { handlerClass ->
                    try {
                        val handlerAnnotation = handlerClass.findAnnotation<Handler>()!!
                        val configName = configNames[handlerAnnotation.configuration]!!
                        if (!Runtime.factories.containsKey(configName)) {
                            Runtime.factories[configName] = mutableMapOf()
                        }
                        Runtime.factories[configName]!![handlerAnnotation.target] =
                            handlerClass.constructors.find { it.parameters.isEmpty() }?.let { { it.call() as AbstractHandler<*> } }
                                ?: throw InvalidObjectException("Hanler class should have only primary constructor without args")
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

    fun verify(source: InputStream, configurationName: String): Result {
        val runtime = timer({ logger.debug { "Runtime initialization done in $it ms" } }) {
            Runtime(
                configurationName,
                configurations
            )
        }
        Document(runtime).apply {
            load(source)
            runVerification()
            return Result(
                ByteArrayOutputStream().also { writeResult(it) },
                this.ctx.render.getString(),
                Statistics(
                    this.ctx.mistakeId
                )
            )
        }
    }
}