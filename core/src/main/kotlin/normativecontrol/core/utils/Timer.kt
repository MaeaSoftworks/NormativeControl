package normativecontrol.core.utils

import normativecontrol.shared.debug
import org.slf4j.Logger

inline fun Logger.timer(prefix: String, body: () -> Unit) {
    val start = System.currentTimeMillis()
    body()
    val end = System.currentTimeMillis()
    this.debug { "$prefix: ${ end - start } ms" }
}