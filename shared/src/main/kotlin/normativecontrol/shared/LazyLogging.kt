package normativecontrol.shared

import org.slf4j.Logger

inline fun Logger.info(message: () -> String) {
    if (this.isInfoEnabled) this.info(message())
}

inline fun Logger.error(message: () -> String) {
    if (this.isErrorEnabled) this.error(message())
}

inline fun Logger.debug(message: () -> String) {
    if (this.isDebugEnabled) this.debug(message())
}

inline fun Logger.warn(message: () -> String) {
    if (this.isWarnEnabled) this.warn(message())
}

inline fun Logger.trace(message: () -> String) {
    if (this.isTraceEnabled) this.trace(message())
}