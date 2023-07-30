package com.maeasoftworks.bootstrap.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

object Functions {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    inline fun <T> retry(maxRetries: Int, timeoutSeconds: Long, predicate: (T?) -> Boolean, action: () -> T): T {
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
