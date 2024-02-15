package ru.maeasoftworks.normativecontrol.core.configurations

import ru.maeasoftworks.normativecontrol.core.utils.lateinitVal

object VerificationConfiguration {
    var forceStyleInlining: Boolean by lateinitVal()

    inline fun initialize(fn: VerificationConfiguration.() -> Unit) {
        fn.invoke(VerificationConfiguration)
    }
}