package normativecontrol.core

import normativecontrol.core.handlers.Factory
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.annotations.HandlerGroup
import normativecontrol.core.handlers.HandlerMapper
import normativecontrol.core.utils.LogColor
import normativecontrol.core.utils.highlight
import normativecontrol.shared.timer
import normativecontrol.shared.debug
import normativecontrol.shared.error
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object Core {
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        val packageName = "normativecontrol"
        logger.debug { "Searching handler factories at '$packageName'..." }
        timer({ logger.debug { "Handlers' initialization done in $it ms" } }) {
            val configNames = mutableMapOf<KClass<*>, String>()

            val reflections = Reflections(packageName)
            reflections.getTypesAnnotatedWith(HandlerGroup::class.java)
                .map { it.kotlin }
                .forEach { configuration ->
                    val setup = configuration.findAnnotation<HandlerGroup>()!!
                    configNames += configuration to setup.name
                }

            reflections.getTypesAnnotatedWith(HandlerFactory::class.java)
                .map { it.kotlin }
                .forEach { factoryObject ->
                    try {
                        val factory = factoryObject.findAnnotation<HandlerFactory>()!!
                        val configName = configNames[factory.configuration]!!
                        if (!HandlerMapper.factories.containsKey(configName)) {
                            HandlerMapper.factories[configName] = mutableMapOf()
                        }
                        HandlerMapper.factories[configName]!![factory.target] = factoryObject.objectInstance as Factory<*>
                        logger.debug {
                            factoryObject.java.declaringClass.kotlin.simpleName!!.highlight(LogColor.ANSI_YELLOW) + " loaded to group " +
                                    configName.highlight(generateColor(configName))
                        }
                    } catch (e: Exception) {
                        logger.error(e) { "Unable to load handler ${factoryObject.qualifiedName}." }
                    }
                }
        }
    }

    private fun generateColor(value: String): LogColor {
        return LogColor.entries[value.hashCode() % LogColor.entries.count()]
    }

    fun verify(source: InputStream, configuration: Configuration<*>): Pair<ByteArrayOutputStream, String> {
        Document(configuration).apply {
            load(source)
            runVerification()
            return ByteArrayOutputStream().also { writeResult(it) } to this.ctx.render.getString()
        }
    }
}