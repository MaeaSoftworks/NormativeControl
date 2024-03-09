package normativecontrol.launcher.client

import org.slf4j.LoggerFactory
import java.io.Closeable

object ApplicationFinalizer {
    private val closeables = mutableListOf<Closeable>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        Runtime.getRuntime().addShutdownHook(Thread(::close, "finalizer"))
    }

    fun add(closeable: Closeable) {
        closeables += closeable
    }

    private fun close() {
        logger.info("Shutting down...")
        closeables.forEach {
            logger.info("Closing ${it::class.qualifiedName!!}")
            it.close()
        }
        logger.info("Shutdown completed!")
    }
}