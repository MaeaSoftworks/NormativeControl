package com.maeasoftworks.normativecontrolcore.bootstrap.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.reflect.KClass

object Functions {
    inline fun <T, reified E> retry(maxRetries: Int, timeoutSeconds: Long, caller: KClass<*>, predicate: (T?) -> Boolean, action: () -> T): T? {
        val logger: Logger = LoggerFactory.getLogger(caller.java)
        var count = 0
        var result: T? = null
        do {
            try {
                logger.info("Attempt ${count++} to run")
                result = action()
            } catch (e: Exception) {
                if (e !is E) {
                    throw e
                }
                Thread.sleep(Duration.ofSeconds(timeoutSeconds))
            }
        } while (!predicate(result) && count < maxRetries)
        return result
    }
}
