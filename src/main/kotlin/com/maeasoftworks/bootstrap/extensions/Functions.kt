package com.maeasoftworks.bootstrap.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.reflect.KClass

object Functions {
    inline fun <T> retry(maxRetries: Int, timeoutSeconds: Long, caller: KClass<*>, predicate: (T?) -> Boolean, action: () -> T): T {
        val logger: Logger = LoggerFactory.getLogger(caller.java)
        var count = 0
        var result: T?
        do {
            logger.info("Attempt ${count++} of call")
            result = action()
            Thread.sleep(Duration.ofSeconds(timeoutSeconds))
        } while (!predicate(result) && count < maxRetries)
        return result!!
    }
}
